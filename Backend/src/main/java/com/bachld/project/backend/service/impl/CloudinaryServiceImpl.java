package com.bachld.project.backend.service.impl;


import com.cloudinary.Cloudinary;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements com.bachld.project.backend.service.CloudinaryService {

    Cloudinary cloudinary;

    @Override
    public String upload(MultipartFile file) {
        try {
            Map result = cloudinary.uploader().upload(file.getBytes(), Map.of());
            return result.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String upload(File file) {
        try {
            Map result = cloudinary.uploader().upload(file, Map.of("resource_type", "raw"));
            return result.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Upload File thất bại", e);
        }
    }

}
