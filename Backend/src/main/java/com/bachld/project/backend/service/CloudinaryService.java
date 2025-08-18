package com.bachld.project.backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface CloudinaryService {

    String upload(MultipartFile file);
    String upload(File file);
    String uploadRawFile(MultipartFile file);
}
