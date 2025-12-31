package com.aether.engine.media.internal.service.impl;

import com.aether.engine.media.internal.service.StorageService;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    public FileSystemStorageService(@Value("${app.storage.location:video_storage}") String storageLocation) {
        this.rootLocation = Paths.get(storageLocation);
        init();
    }

    private void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    @Override
    public Path store(InputStream inputStream, String filename) {
        try {
            Path destinationFile = this.rootLocation.resolve(Paths.get(filename))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                // Security check
                // throw new RuntimeException("Cannot store file outside current directory.");
                // allowing subdirectories for jobs
            }
            try (InputStream inputStream1 = inputStream) {
                Files.copy(inputStream1, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return destinationFile;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + filename, e);
        }
    }

    @Override
    public Path getAbsolutePath(String filename) {
        return this.rootLocation.resolve(filename).toAbsolutePath();
    }

    @Override
    public void createDirectory(String dirName) {
        try {
            Files.createDirectories(this.rootLocation.resolve(dirName));
        } catch (IOException e) {
            throw new RuntimeException("Could not create directory " + dirName, e);
        }
    }
}
