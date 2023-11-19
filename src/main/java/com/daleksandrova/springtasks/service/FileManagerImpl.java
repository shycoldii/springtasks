package com.daleksandrova.springtasks.service;

import com.daleksandrova.springtasks.exception.FileStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Имплементация интерфейс, управляющего чтением и записью бинарного контента в файловую систему.
 *
 * @author Darya Alexandrova
 * @since 2023.11.18
 */
@Service
public class FileManagerImpl implements FileManager {

    @Value("${dataPath}")
    private String dataPath;

    /**
     * Логгер.
     */
    private static final Logger LOG = LoggerFactory.getLogger(FileManagerImpl.class);

    /**
     * Папка хранения файлов.
     */
    private File storageDir;

    /**
     * Папка хранения мета-информации.
     */
    private File metaDir;

    /**
     * Папка хранения временного контента.
     */
    private File tmpDir;

    /**
     * Инициализация папок.
     */
    @PostConstruct
    public void postConstruct() {
        if (!dataPath.startsWith("/")
                && !dataPath.startsWith("\\")
                && !dataPath.contains("://")
                && !dataPath.contains(":\\")) {
            throw new IllegalStateException("Not absolute root path " + dataPath);
        }
        final File base = new File(dataPath);
        storageDir = new File(base, "data");
        tmpDir = new File(base, "tmp");
        metaDir = new File(base, "meta");

        if (!storageDir.mkdirs()) {
            LOG.debug("Failed to make dir {}", storageDir.getName());
        }
        if (!tmpDir.mkdirs()) {
            LOG.debug("Failed to make dir {}", tmpDir.getName());
        }
        if (!metaDir.mkdirs()) {
            LOG.debug("Failed to make dir {}", metaDir.getName());
        }
    }

    @Override
    @Transactional
    public String store(InputStream inputStream) throws FileStorageException {
        File tmp = null;

        try {
            String fileId = UUID.randomUUID().toString();
            tmp = File.createTempFile(fileId, ".tmp", tmpDir);
            storeTmpFile(inputStream, tmp);

            File file = new File(storageDir, fileId);
            Files.move(tmp.toPath(), file.toPath(), StandardCopyOption.ATOMIC_MOVE);

            LOG.debug("Return new file {}", fileId);
            return fileId;
        } catch (IOException ex) {
            throw new FileStorageException(ex);
        } finally {
            if (tmp != null && tmp.exists() && !tmp.delete()) {
                LOG.error("Failed to delete temp file {}", tmp.getName());
            }
        }
    }

    @Override
    @Transactional
    public void storeMetaInformation(InputStream inputStream, String fileName) {
        File metaTmpFile = null;
        try {
            metaTmpFile = File.createTempFile(fileName, ".tmp", metaDir);
            storeTmpFile(inputStream, metaTmpFile);

            File file = new File(metaDir, fileName + ".json");
            Files.move(metaTmpFile.toPath(), file.toPath(), StandardCopyOption.ATOMIC_MOVE);

            LOG.debug("Stored meta file {}", fileName);
        } catch (IOException e) {
            LOG.error("Failed to store meta file with id {}. Cause: {}", fileName, e.getCause());
        } finally {
            if (metaTmpFile != null && metaTmpFile.exists() && !metaTmpFile.delete()) {
                LOG.error("Failed to delete temp meta file {}", metaTmpFile.getName());
            }
        }
    }

    /**
     * Записывает контент в временный файл.
     *
     * @param inputStream контент
     * @param tmp         временный контент
     * @throws IOException ошибка при записи
     */
    private void storeTmpFile(InputStream inputStream, File tmp) throws IOException {
        try (OutputStream out = Files.newOutputStream(tmp.toPath())) {
            inputStream.transferTo(out);
            out.flush();
        }
    }
}
