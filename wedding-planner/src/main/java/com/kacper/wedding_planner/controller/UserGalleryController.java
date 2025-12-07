package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.model.Photo;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.PhotoRepository;
import com.kacper.wedding_planner.repository.UserRepository;
import com.kacper.wedding_planner.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/gallery")
public class UserGalleryController {

    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorage;

    public UserGalleryController(PhotoRepository photoRepository,
                                 UserRepository userRepository,
                                 FileStorageService fileStorage) {
        this.photoRepository = photoRepository;
        this.userRepository = userRepository;
        this.fileStorage = fileStorage;
    }

    @GetMapping
    public String gallery(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono użytkownika" + principal.getUsername()));

        List<Photo> photos = photoRepository.findByOwnerId(user.getId());
        photos.sort(Comparator.comparing(Photo::getUploadedAt).reversed());

        model.addAttribute("photos", photos);
        return "gallery_list";
    }

    @GetMapping("/image/{id}")
    @ResponseBody
    public ResponseEntity<Resource> serveImage(@PathVariable Long id,
                                               @AuthenticationPrincipal UserDetails principal) {
        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono użytkownika" + principal.getUsername()));

        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono zdjęcia o ID: " + id));

        if (!photo.getOwner().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Brak dostępu do tego zdjęcia");
        }

        Path path = fileStorage.loadAsPath(photo.getStoragePath());
        Resource resource;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Niepoprawna ścieżka pliku");
        }

        if (!resource.exists() || !resource.isReadable()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Plik nie istnieje lub jest nieczytelny");
        }

        String safeFilename = URLEncoder.encode(photo.getFilename(), StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + photo.getFilename() + "\"")
                .body(resource);
    }

}
