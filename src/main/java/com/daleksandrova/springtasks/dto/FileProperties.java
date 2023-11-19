package com.daleksandrova.springtasks.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Контейнер свойств файла.
 *
 * @author Darya Alexandrova
 * @since 2023.11.17
 */
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class FileProperties {

    /**
     * Имя файла.
     */
    private String name;

    /**
     * Тип файла.
     */
    private String contentType;

    /**
     * Размер содержимого.
     */
    private long contentSize;
}