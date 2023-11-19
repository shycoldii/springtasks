package com.daleksandrova.springtasks.listener;

import com.daleksandrova.springtasks.entity.FileEntity;
import com.daleksandrova.springtasks.service.FileManager;
import com.daleksandrova.springtasks.util.AutowireHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
    @Autowired
    private FileManager fileManager;


    /**
     * Обработчик, вызываемый перед сохранением нового файла в хранилище.
     *
     * @param fileEntity файл
     */
    @PrePersist
    public void onPrePersist(final FileEntity fileEntity) {
        initAutowiredFields();
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
        initAutowiredFields();
        final InputStream inputStream = convertFileEntityToJsonStream(fileEntity);
        if (inputStream != null) {
            fileManager.storeMetaInformation(inputStream, fileEntity.getId());
        }
    }

    /**
     * Заполняет свойства объекта, помеченные аннотацией Autowired.
     * <p/>
     * По спецификации JPA подобные слушатели не содержат состояния, за создание объекта слушателей отвечает ORM. Это не
     * даёт возможности стандартными средствами поместить в него такие поля.
     * Можно, конечно, сделать конструктор с параметром, но тогда мы не избавимся от необходимости
     * иметь дефолтный конструктор.
     */
    protected void initAutowiredFields() {
        AutowireHelper.autowire(this, fileManager);
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

            String jsonString = objectMapper.writeValueAsString(fileEntity);
            byte[] jsonBytes = jsonString.getBytes();

            return new ByteArrayInputStream(jsonBytes);
        } catch (JsonProcessingException e) {
            // do nothing
            LOG.error("Json processing error :c", e);
        }
        return null;
    }
}

