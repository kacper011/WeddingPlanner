package com.kacper.wedding_planner.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface FileStorageService {

    String storeFile(MultipartFile file, Long userId) throws IOException;

    Path loadAsPath(String relative);

    boolean delete(String relative) throws IOException;
}
