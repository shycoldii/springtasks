package com.daleksandrova.springtasks.service;

import com.daleksandrova.springtasks.dto.FileProperties;

import java.io.InputStream;

/**
 * Интерфейс сервиса работы с файлами.
 */
public interface FileService {

    /**
     * Добавляет в хранилище загруженный файл, возвращая его идентификатор.
     *
     * @param fileProperties свойства добавляемого файла
     * @param contentStream  поток, из которого будет считано содержимое файла
     * @return идентификатор сохраненного ресурса
     */
    String addFile(FileProperties fileProperties, InputStream contentStream);
}