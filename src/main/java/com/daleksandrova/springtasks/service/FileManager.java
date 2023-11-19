package com.daleksandrova.springtasks.service;

import com.daleksandrova.springtasks.exception.FileStorageException;

import java.io.InputStream;

/**
 * Интерфейс, управляющий чтением и записью бинарного контента в файловую систему.
 *
 * @author Darya Alexandrova
 * @since 2023.11.18
 */
public interface FileManager {

    /**
     * Сохранить файл.
     *
     * @param inputStream входящий поток
     * @return идентификатором хранения в файловой системе
     * @throws FileStorageException при ошибках работы с файловой системой
     */
    String store(InputStream inputStream) throws FileStorageException;

    /**
     * Сохранить метаинформацию о файле.
     *
     * @param inputStream входящий поток
     * @param fileName имя мета-файла
     * @throws FileStorageException при ошибках работы с файловой системой
     */
    void storeMetaInformation(InputStream inputStream, String fileName) throws FileStorageException;
}
