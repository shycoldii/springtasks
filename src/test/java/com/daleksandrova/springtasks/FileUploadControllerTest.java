package com.daleksandrova.springtasks;

import com.daleksandrova.springtasks.task2.controller.FileUploadController;
import com.daleksandrova.springtasks.task2.dto.FileProperties;
import com.daleksandrova.springtasks.task2.entity.FileEntity;
import com.daleksandrova.springtasks.task2.exception.FileStorageException;
import com.daleksandrova.springtasks.task2.service.FileManager;
import com.daleksandrova.springtasks.task2.service.FileService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.assertj.core.api.Assertions;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;

/**
 * Тест для проверки синхронного сохранения файла в БД и файловую систему.
 *
 * @author Darya Alexandrova
 * @since 2023.11.15
 */
@SpringBootTest
@AutoConfigureMockMvc
public class FileUploadControllerTest {

    /**
     * Местоположение данных.
     */
    @Value("${dataPath}")
    private String dataPath;

    /**
     * Мокмвс.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Менеджер сущностей.
     */
    @PersistenceContext
    EntityManager entityManager;

    /**
     * Делаем спай для менеджера файлов, чтобы протестировать краевую ситуацию.
     */
    @SpyBean
    FileManager fileManager;

    /**
     * Делаем спай для сервиса файлов, чтобы протестировать краевую ситуацию.
     */
    @SpyBean
    FileService fileService;

    /**
     * Создатель запросов.
     */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Тест, проверяющий корректное сохранение файла в БД и в файловой системе.
     *
     * @throws Exception исключение
     */
    @Test
    public void testPositiveFileUpload() throws Exception {
        // Загружаем тестовый файл
        final String content = "Hello, World!";
        final String fileName = "test.txt";
        final String contentType = "text/plain";

        MockMultipartFile file = new MockMultipartFile("file", fileName, contentType, content.getBytes());

        // Обращаемся к контроллеру
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart(FileUploadController.FILE_API_PATH + FileUploadController.FILE_UPLOAD_PATH)
                .file(file))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // Считываем ответ
        String resultString = result.getResponse().getContentAsString();
        String fileId = new JSONObject(resultString).getString("fileId");

        // Проверяем, что сохранилась сущность FileEntity в БД
        FileEntity savedFileEntity = entityManager.find(FileEntity.class, fileId);
        assertNotNull(savedFileEntity);
        assertThat(savedFileEntity.getContentSize()).isEqualTo(content.length());
        assertThat(savedFileEntity.getName()).isEqualTo(fileName);
        assertThat(savedFileEntity.getMimeType()).isEqualTo(contentType);
        assertThat(savedFileEntity.getContent()).isNull();
        assertThat(jdbcTemplate.queryForList("select * from file_entity").size()).isEqualTo(1);

        // Проверяем, что в папке "meta" есть JSON файл
        Path metaFilePath = Paths.get(dataPath + "/meta", fileId + ".json");
        assertTrue(Files.exists(metaFilePath));
        String jsonContent = new String(Files.readAllBytes(metaFilePath));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        JsonNode jsonNode = objectMapper.readTree(jsonContent);
        JsonNode jsonNodeFromModel = objectMapper.readTree(objectMapper.writeValueAsString(savedFileEntity));
        assertThat(jsonNode).isEqualTo(jsonNodeFromModel);

        // Проверяем, что в папке "data" есть файл с содержимым
        Path dataFilePath = Paths.get(dataPath + "/data", savedFileEntity.getExternalId());
        assertTrue(Files.exists(dataFilePath));
        assertThat(Files.readAllBytes(dataFilePath)).isEqualTo(content.getBytes());
    }


    /**
     * Тест, проверяющий синхронизацию сохранения файла в БД и в ОС.
     * 1. Специально сымитируем исключение при сохранении файла в ОС
     * 2. Проверим, что сущность в БД не сохранилась
     * 3. Проверим, что файл с содержимым и его метаданные не сохранились
     *
     * @throws Exception исключение
     */
    @Test
    public void testNegativeFileUploadWhenStoreFile() throws Exception {
        doThrow(new FileStorageException(new IOException("Some exception"))).when(fileManager).store(ArgumentMatchers.any(InputStream.class));

        // Загружаем тестовый файл
        final String content = "Hello, World again!";
        final String fileName = "test.txt";
        final String contentType = "text/plain";

        MockMultipartFile file = new MockMultipartFile("file", fileName, contentType, content.getBytes());

        // Обращаемся к контроллеру, ожидаем ошибку
        Assertions
                .assertThatThrownBy(() -> mockMvc.perform(MockMvcRequestBuilders.multipart(FileUploadController.FILE_API_PATH + FileUploadController.FILE_UPLOAD_PATH)
                        .file(file))
                        .andExpect(MockMvcResultMatchers.status().is5xxServerError())).hasCauseInstanceOf(FileStorageException.class);

        // Данных в БД нет
        assertThat(jdbcTemplate.queryForList("select * from file_entity").size()).isEqualTo(0);

        // Проверяем, что в папке "meta" НЕТ JSON файла
        Path metaFolder = Paths.get(dataPath + "/meta");
        assertThat(Files.list(metaFolder).count()).isEqualTo(0);

        // Проверяем, что в папке "data" НЕТ файла с содержимым
        Path dataFolder = Paths.get(dataPath + "/data");
        assertThat(Files.list(dataFolder).count()).isEqualTo(0);
    }

    /**
     * Тест, проверяющий синхронизацию сохранения файла в БД и в ОС.
     * 1. Специально сымитируем исключение при сохранении сущности в БД
     * 2. Проверим, что сущность в БД не сохранилась
     * 3. Проверим, что файл с содержимым и его метаданные не сохранились
     *
     * @throws Exception исключение
     */
    @Test
    public void testNegativeFileUploadWhenStoreToBD() throws Exception {
        doThrow(new RuntimeException("Some exception")).when(fileService).addFile((ArgumentMatchers.any(FileProperties.class)), ArgumentMatchers.any(InputStream.class));

        // Загружаем тестовый файл
        final String content = "Hello, World again and again...!";
        final String fileName = "test.txt";
        final String contentType = "text/plain";

        MockMultipartFile file = new MockMultipartFile("file", fileName, contentType, content.getBytes());

        // Обращаемся к контроллеру, ожидаем ошибку
        Assertions
                .assertThatThrownBy(() -> mockMvc.perform(MockMvcRequestBuilders.multipart(FileUploadController.FILE_API_PATH + FileUploadController.FILE_UPLOAD_PATH)
                        .file(file))
                        .andExpect(MockMvcResultMatchers.status().is5xxServerError())).hasCauseInstanceOf(RuntimeException.class);

        // Данных в БД нет
        assertThat(jdbcTemplate.queryForList("select * from file_entity").size()).isEqualTo(0);

        // Проверяем, что в папке "meta" НЕТ JSON файла
        Path metaFolder = Paths.get(dataPath + "/meta");
        assertThat(Files.list(metaFolder).count()).isEqualTo(0);

        // Проверяем, что в папке "data" НЕТ файла с содержимым
        Path dataFolder = Paths.get(dataPath + "/data");
        assertThat(Files.list(dataFolder).count()).isEqualTo(0);
    }

    /**
     * Удаляем данные, созданные тестами.
     *
     * @throws IOException исключение
     */
    @AfterEach
    public void cleanup() throws IOException {
        jdbcTemplate.update("delete file_entity");

        Files.walk(Paths.get(dataPath + "/data"))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .forEach(File::delete);

        Files.walk(Paths.get(dataPath + "/meta"))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .forEach(File::delete);
    }

}
