package com.bachld.project.backend;

import com.bachld.project.backend.entity.TaiKhoan;
import com.bachld.project.backend.enums.Role;
import com.bachld.project.backend.repository.TaiKhoanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class BackendApplication {
    private static final Logger log = LoggerFactory.getLogger(BackendApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner createAdminUser(TaiKhoanRepository taiKhoanRepository) {
        return args -> {
            if(taiKhoanRepository.findByEmail("vpk@tlu.edu.vn").isEmpty()) {
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
                TaiKhoan taiKhoan = TaiKhoan.builder()
                        .email("vpk@tlu.edu.vn")
                        .matKhau(passwordEncoder.encode("123456"))
                        .vaiTro(Role.ADMIN)
                        .build();
                taiKhoanRepository.save(taiKhoan);
            }
//            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
//            log.info(passwordEncoder.encode("123456"));
        };
    }

}
