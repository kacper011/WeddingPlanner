package com.kacper.wedding_planner.service;

import com.kacper.wedding_planner.model.Photo;
import com.kacper.wedding_planner.model.UploadToken;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PhotoService {

    Photo savePhotoFromUpload(MultipartFile file, UploadToken token) throws IOException;
}
