package com.daleksandrova.springtasks.service;

import com.daleksandrova.springtasks.dto.FileProperties;
import com.daleksandrova.springtasks.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.util.UUID;

/**
 * Имплементация сервиса по работе с файлами.
 *
 * @author Darya Alexandrova
 * @since 2023.11.17
 */
@Service
public class FileServiceImpl implements FileService {

    /**
     * Менеджер сущностей.
     */
    private final EntityManager entityManager;

    /**
     * Конструктор.
     *
     * @param jpaContext контекст
     */
    public FileServiceImpl(JpaContext jpaContext) {
        this.entityManager = jpaContext.getEntityManagerByManagedType(FileEntity.class);
    }

    @Override
    public String addFile(FileProperties fileProperties, InputStream contentStream) {
        final String fileId = UUID.randomUUID().toString();
        FileEntity fileEntity = createFileEntity(fileProperties, contentStream, fileId);

        entityManager.persist(fileEntity);
        entityManager.flush();
        entityManager.refresh(fileEntity);
        return fileId;
    }

    /**
     * Создает сущность файла.
     *
     * @param fileProperties свойства
     * @param contentStream  контент
     * @param fileId         идентификатор
     * @return сущность файла
     */
    private FileEntity createFileEntity(FileProperties fileProperties, InputStream contentStream, String fileId) {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setId(fileId);
        fileEntity.setName(fileProperties.getName());
        fileEntity.setMimeType(fileProperties.getContentType());
        fileEntity.setContentSize(fileProperties.getContentSize());
        fileEntity.setContent(new InputStreamBlobWrapper(contentStream, fileProperties.getContentSize()));

        return fileEntity;
    }

    /**
     * Обёртка над {@link InputStream}, позволяющая работать с ним, как с {@link Blob}.
     */
    private static class InputStreamBlobWrapper implements Blob {

        /**
         * Оборачиваемый поток.
         */
        private final InputStream contentStream;

        /**
         * Размер контента, который может быть прочитан из оборачиваемого потока.
         */
        private final long length;

        /**
         * Constructor.
         *
         * @param contentStream оборачиваемый поток
         * @param length        размер контента, который может быть прочитан из оборачиваемого потока
         */
        private InputStreamBlobWrapper(InputStream contentStream, long length) {
            this.length = length;
            this.contentStream = contentStream;
        }

        @Override
        public long length() {
            return length;
        }

        @Override
        public InputStream getBinaryStream() {
            return contentStream;
        }

        @Override
        public InputStream getBinaryStream(long pos, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte[] getBytes(long pos, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long position(@SuppressWarnings("null") byte[] pattern, long start) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long position(@SuppressWarnings("null") Blob pattern, long start) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int setBytes(long pos, @SuppressWarnings("null") byte[] bytes) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int setBytes(long pos, @SuppressWarnings("null") byte[] bytes, int offset, int len) {
            throw new UnsupportedOperationException();
        }

        @Override
        public OutputStream setBinaryStream(long pos) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void truncate(long len) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void free() {
            throw new UnsupportedOperationException();
        }
    }
}
