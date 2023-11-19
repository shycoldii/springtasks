package com.daleksandrova.springtasks.task2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * ДТО файла.
 *
 * @author Darya Alexandrova
 * @since 2023.11.17
 */
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class FileEntityDto {

    /**
     * Идентификатор.
     */
    private String id;

    /**
     * Имя файла.
     */
    private String name;

    /**
     * Тип файла.
     */
    private String mimeType;

    /**
     * Внешний идентификатор контента.
     */
    private String externalId;

    /**
     * Время создания.
     */
    private LocalDateTime creationTime;

    /**
     * Размер содержимого.
     */
    private long contentSize;
}
