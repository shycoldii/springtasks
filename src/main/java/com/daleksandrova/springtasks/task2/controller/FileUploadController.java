package com.daleksandrova.springtasks.task2.controller;

import com.daleksandrova.springtasks.task2.dto.FileProperties;
import com.daleksandrova.springtasks.task2.dto.UploadResponse;
import com.daleksandrova.springtasks.task2.service.FileService;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.daleksandrova.springtasks.task2.controller.FileUploadController.FILE_API_PATH;

/**
 * Контроллер для работы с файлами.
 *
 * @author Darya Alexandrova
 * @since 2023.11.17
 */
@RestController
@RequestMapping(FILE_API_PATH)
public class FileUploadController {

    /**
     * Путь для работы с файлами.
     */
    public static final String FILE_API_PATH = "/file_api";

    /**
     * Путь для загрузки файлов.
     */
    public static final String FILE_UPLOAD_PATH = "/upload";

    /**
     * Сервис файлов.
     */
    private final FileService fileService;

    /**
     * Конструктор.
     *
     * @param fileService сервис файлов
     */
    public FileUploadController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * Загружает файл.
     *
     * @param file     файл
     * @param response ответ
     * @return ответ загрузки
     * @throws IOException исключение
     */
    @RequestMapping(value = FILE_UPLOAD_PATH, method = {RequestMethod.POST},
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @Transactional
    public UploadResponse uploadFile(@RequestParam("file") final MultipartFile file,
                                     final HttpServletResponse response) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        final FileProperties fileProperties = new FileProperties(file.getOriginalFilename(), file.getContentType(), file.getSize());
        final String fileId = fileService.addFile(fileProperties, file.getInputStream());
        return new UploadResponse(true, "UPLOAD OK", fileId);
    }
}
