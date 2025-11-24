package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.model.Photo;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.PhotoRepository;
import com.kacper.wedding_planner.repository.UserRepository;
import com.kacper.wedding_planner.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
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
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();

        List<Photo> photos = photoRepository.findByOwnerId(user.getId());
        photos.sort((a, b) -> b.getUploadedAt().compareTo(a.getUploadedAt()));
        model.addAttribute("photos", photos);

        return "gallery/list";
    }

    @GetMapping("/image/{id}")
    @ResponseBody
    public Resource serveImage(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) throws Exception {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        Photo photo = photoRepository.findById(id).orElseThrow();

        if (!photo.getOwner().getId().equals(user.getId())) {
            throw new SecurityException("Access denied");
        }

        Path path = fileStorage.loadAsPath(photo.getFilename());
        return new UrlResource(path.toUri());
    }
}
