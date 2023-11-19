package com.daleksandrova.springtasks.exception;

/**
 * Исключение, выбрасываемое при ошибках работы с хранилищем контента.
 */
public class FileStorageException extends RuntimeException {

    /**
     * Сообщение.
     */
    private static final String MESSAGE = "Ошибка работы с хранилищем файлов";

    /**
     * Конструктор.
     *
     * @param ex исключение
     */
    public FileStorageException(Exception ex) {
        super(MESSAGE, ex);
    }

}