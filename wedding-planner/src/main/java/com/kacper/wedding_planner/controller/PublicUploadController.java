package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.model.UploadToken;
import com.kacper.wedding_planner.repository.UploadTokenRepository;
import com.kacper.wedding_planner.service.PhotoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Controller
@RequestMapping("/public")
public class PublicUploadController {

    private final UploadTokenRepository tokenRepository;
    private final PhotoService photoService;
    private static final Logger log = LoggerFactory.getLogger(PublicUploadController.class);


    public PublicUploadController(UploadTokenRepository tokenRepository, PhotoService photoService) {
        this.tokenRepository = tokenRepository;
        this.photoService = photoService;
    }

    @GetMapping("/upload/{token}")
    public String showUploadForm(@PathVariable String token, Model model) {
        Optional<UploadToken> ot = tokenRepository.findByToken(token);

        if (ot.isEmpty() || !ot.get().isValid()) {
            return "public_invalid_token";
        }

        model.addAttribute("token", token);
        return "public_upload_form";
    }

    @PostMapping("/upload/{token}")
    public String upload(@PathVariable String token,
                         @RequestParam("file") MultipartFile file,
                         Model model) {

        Optional<UploadToken> ot = tokenRepository.findByToken(token);

        if (ot.isEmpty() || !ot.get().isValid()) {
            log.warn("Nieprawidłowy token publiczny: {}", token);
            return "public_invalid_token";
        }

        if (file.isEmpty() || !file.getContentType().startsWith("image/") || file.getSize() > 5*1024*1024) {
            model.addAttribute("error", "Nieprawidłowy plik");
            model.addAttribute("token", token);
            return "public_upload_form";
        }

        try {
            photoService.savePhotoFromUpload(file, ot.get());
            return "public_upload_success";
        } catch (Exception ex) {
            log.error("Błąd zapisu pliku: {}", ex.getMessage(), ex);
            model.addAttribute("error", "Nie udało się zapisać pliku");
            model.addAttribute("token", token);
            return "public_upload_form";
        }
    }

}
