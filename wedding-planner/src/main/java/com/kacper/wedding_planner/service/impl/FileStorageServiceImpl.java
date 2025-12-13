package com.kacper.wedding_planner.service.impl;

import com.kacper.wedding_planner.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path root;

    public FileStorageServiceImpl(@Value("${app.upload.dir:/app/uploads}") String uploadDir) throws IOException {

        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();

        if (!Files.exists(this.root)) {
            Files.createDirectories(this.root);
        }
    }

    @Override
    public String storeFile(MultipartFile file, Long userId) throws IOException {

        if (file.isEmpty()) {
            throw new IOException("Pusty plik");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IOException("Plik jest za duży (max 10MB)");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("Niedozwolony typ pliku");
        }

        String original = StringUtils.cleanPath(file.getOriginalFilename());
        if (original.contains("..")) {
            throw new IOException("Niepoprawna nazwa pliku");
        }

        String ext = "";
        int idx = original.lastIndexOf('.');
        if (idx >= 0) {
            ext = original.substring(idx).toLowerCase();
        }

        String storedName = UUID.randomUUID() + ext;
        Path userDir = root.resolve(String.valueOf(userId));
        Files.createDirectories(userDir);

        Path target = userDir.resolve(storedName);
        Files.copy(file.getInputStream(), target);

        return userId + "/" + storedName;
    }

    @Override
    public Path loadAsPath(String relative) {

        Path path = root.resolve(relative).normalize();

        if (!path.startsWith(root)) {
            throw new SecurityException("Nieautoryzowany dostęp do pliku: " + relative);
        }

        return path;
    }

    @Override
    public boolean delete(String relative) throws IOException {
        Path p = loadAsPath(relative);
        return Files.deleteIfExists(p);
    }
}
