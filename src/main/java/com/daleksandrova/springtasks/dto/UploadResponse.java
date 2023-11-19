package com.daleksandrova.springtasks.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ДТО ответа загрузки файла.
 *
 * @author Darya Alexandrova
 * @since 2023.11.17
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponse {

    /**
     * Успех/нет.
     */
    private boolean success;

    /**
     * Сообщение.
     */
    private String message;

    /**
     * Идентификатор файла.
     */
    private String fileId;

    public UploadResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}