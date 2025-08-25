package com.bachld.project.backend.controller;

import com.bachld.project.backend.dto.ApiResponse;
import com.bachld.project.backend.dto.request.auth.IntrospectRequest;
import com.bachld.project.backend.dto.request.auth.LoginRequest;
import com.bachld.project.backend.dto.request.auth.LogoutRequest;
import com.bachld.project.backend.dto.response.auth.InfoResponse;
import com.bachld.project.backend.dto.response.auth.IntrospectResponse;
import com.bachld.project.backend.dto.response.auth.LoginResponse;
import com.bachld.project.backend.service.AuthService;
import com.nimbusds.jose.*;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthController {

    AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        return ApiResponse.<LoginResponse>builder()
                .result(authService.login(loginRequest))
                .build();
    }

    @PostMapping("/introspect")
    public  ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request){
        return ApiResponse.<IntrospectResponse>builder()
                .result(authService.introspect(request))
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authService.logout(request);
        return ApiResponse.<String>builder()
                .result("Logout successfully!")
                .build();
    }

    @GetMapping("/my-info")
    public ApiResponse<InfoResponse> getMyInfo() {
        return ApiResponse.<InfoResponse>builder()
                .result(authService.getMyInfo())
                .build();
    }
}
