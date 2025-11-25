package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.model.UploadToken;
import com.kacper.wedding_planner.repository.UploadTokenRepository;
import com.kacper.wedding_planner.service.PhotoService;
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
    public String upload(
            @PathVariable String token,
            @RequestParam("file") MultipartFile file,
            Model model
    ) {
        Optional<UploadToken> ot = tokenRepository.findByToken(token);

        if (ot.isEmpty() || !ot.get().isValid()) {
            return "public_invalid_token";
        }

        try {
            photoService.savePhotoFromUpload(file, ot.get());
            return "public/upload_success";

        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("token", token);
            return "public_upload_form";
        }
    }
}
