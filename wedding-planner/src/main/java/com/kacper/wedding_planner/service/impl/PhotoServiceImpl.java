package com.kacper.wedding_planner.service.impl;

import com.kacper.wedding_planner.model.Photo;
import com.kacper.wedding_planner.model.UploadToken;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.PhotoRepository;
import com.kacper.wedding_planner.repository.UploadTokenRepository;
import com.kacper.wedding_planner.service.FileStorageService;
import com.kacper.wedding_planner.service.PhotoService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class PhotoServiceImpl implements PhotoService {

    private final FileStorageService fileStorage;
    private final PhotoRepository photoRepository;
    private final UploadTokenRepository tokenRepository;

    public PhotoServiceImpl(FileStorageService fileStorage, PhotoRepository photoRepository, UploadTokenRepository tokenRepository) {
        this.fileStorage = fileStorage;
        this.photoRepository = photoRepository;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public Photo savePhotoFromUpload(MultipartFile file, UploadToken token) throws IOException {

        if (token == null || !token.isValid()) {
            throw new IllegalArgumentException("Token jest nieważny lub wygasł");
        }

        if (file.isEmpty()) throw new IllegalArgumentException("Plik pusty");

        if (file.getSize() > 5L * 1024 * 1024) throw new IllegalArgumentException("Plik za duży (max 5MB)");

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("image/"))) {
            throw new IllegalArgumentException("Dozwolone tylko obrazy");
        }

        User user = token.getOwner();
        String storedRelative = fileStorage.storeFile(file, user.getId());

        Photo p = new Photo();
        p.setFilename(file.getOriginalFilename());
        p.setStoragePath(storedRelative);
        p.setContentType(contentType);
        p.setSize(file.getSize());
        p.setUploadedAt(LocalDateTime.now());
        p.setOwner(user);
        p.setUploadedByPublic(true);

        Photo saved = photoRepository.save(p);

        token.markUsed();
        tokenRepository.save(token);

        return saved;
    }
}
