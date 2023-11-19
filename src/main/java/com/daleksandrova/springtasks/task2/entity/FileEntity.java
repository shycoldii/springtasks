package com.daleksandrova.springtasks.task2.entity;

import com.daleksandrova.springtasks.task2.listener.FileStorageListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.sql.Blob;
import java.time.LocalDateTime;

/**
 * Сущность файла.
 *
 * @author Darya Alexandrova
 * @since 2023.11.17
 */
@Entity
@Table(name = "file_entity")
@EntityListeners({AuditingEntityListener.class, FileStorageListener.class})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FileEntity {

    /**
     * Идентификатор.
     */
    @Id
    private String id;

    /**
     * Имя файла.
     */
    @Column
    private String name;

    /**
     * Тип файла.
     */
    @Column
    private String mimeType;

    /**
     * Внешний идентификатор контента.
     */
    @Column
    private String externalId;

    /**
     * Время создания.
     */
    @CreatedDate
    @Column
    private LocalDateTime creationTime;

    /**
     * Размер содержимого.
     */
    @Column
    private long contentSize;

    /**
     * Содержимое.
     * Подтирается после создания файла.
     */
    @Lob
    @JsonIgnore
    private Blob content;
}
