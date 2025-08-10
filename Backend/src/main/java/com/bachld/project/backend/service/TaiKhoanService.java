package com.bachld.project.backend.service;

import com.bachld.project.backend.dto.response.taikhoan.AnhDaiDienUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface TaiKhoanService {

    AnhDaiDienUploadResponse uploadAnhDaiDien(MultipartFile file) throws IOException;

}
