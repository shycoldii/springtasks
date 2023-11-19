package com.daleksandrova.springtasks.task2.listener;

import com.daleksandrova.springtasks.task2.dto.FileEntityDto;
import com.daleksandrova.springtasks.task2.entity.FileEntity;
import com.daleksandrova.springtasks.task2.service.FileManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import static java.util.Objects.requireNonNull;

/**
 * Слушатель событий файлов.
 *
 * @author Darya Alexandrova
 * @since 2023.11.18
 */
public class FileStorageListener {

    /**
     * Логгер.
     */
    private static final Logger LOG = LoggerFactory.getLogger(FileStorageListener.class);

    /**
     * Менеджер файлов.
     */
    private final FileManager fileManager;

    /**
     * Конструктор.
     *
     * @param fileManager менеджер файлов
     */
    public FileStorageListener(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    /**
     * Обработчик, вызываемый перед сохранением нового файла в хранилище.
     *
     * @param fileEntity файл
     */
    @PrePersist
    public void onPrePersist(final FileEntity fileEntity) {
        final long contentSize = fileEntity.getContentSize();
        if (contentSize == 0) {
            return;
        }

        try (final InputStream inputStream = requireNonNull(fileEntity.getContent()).getBinaryStream()) {
            String externalId = fileManager.store(requireNonNull(inputStream));
            fileEntity.setContent(null);
            fileEntity.setExternalId(externalId);
        } catch (IOException | SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Обработчик, вызываемый после загрузки объекта контейнера контента из хранилища.
     * Необходимо сохранить метаинформацию о сущности.
     * По требованиям любая ошибка, связанная с этим файлом, не имеет влияния на сохраненный контент и его сущность.
     *
     * @param fileEntity файл
     */
    @PostLoad
    public void onPostLoad(final FileEntity fileEntity) {
        final InputStream inputStream = convertFileEntityToJsonStream(fileEntity);
        if (inputStream != null) {
            fileManager.storeMetaInformation(inputStream, fileEntity.getId());
        }
    }

    /**
     * Конвертирует файл в json.
     *
     * @param fileEntity сущность файла
     * @return стрим
     */
    private InputStream convertFileEntityToJsonStream(FileEntity fileEntity) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            String jsonString = objectMapper.writeValueAsString(createAndReturnFileEntityDto(fileEntity));
            byte[] jsonBytes = jsonString.getBytes();

            return new ByteArrayInputStream(jsonBytes);
        } catch (JsonProcessingException e) {
            // do nothing
            LOG.error("Json processing error :c", e);
        }
        return null;
    }

    /**
     * Создает на основе сущности файла ДТО.
     *
     * @param fileEntity сущность файла
     * @return ДТО файла
     */
    private FileEntityDto createAndReturnFileEntityDto(FileEntity fileEntity) {
        FileEntityDto fileEntityDto = new FileEntityDto();

        fileEntityDto.setId(fileEntity.getId());
        fileEntityDto.setName(fileEntity.getName());
        fileEntityDto.setContentSize(fileEntity.getContentSize());
        fileEntityDto.setExternalId(fileEntity.getExternalId());
        fileEntityDto.setMimeType(fileEntity.getMimeType());
        fileEntityDto.setCreationTime(fileEntity.getCreationTime());

        return fileEntityDto;
    }
}

