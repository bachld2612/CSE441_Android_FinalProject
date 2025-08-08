package com.bachld.project.backend.service;

import com.bachld.project.backend.dto.request.auth.IntrospectRequest;
import com.bachld.project.backend.dto.request.auth.LoginRequest;
import com.bachld.project.backend.dto.request.auth.LogoutRequest;
import com.bachld.project.backend.dto.response.auth.InfoResponse;
import com.bachld.project.backend.dto.response.auth.IntrospectResponse;
import com.bachld.project.backend.dto.response.auth.LoginResponse;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthService {

    LoginResponse login(LoginRequest loginRequest);
    IntrospectResponse introspect(IntrospectRequest request);
    void logout(LogoutRequest request) throws ParseException, JOSEException;
    InfoResponse getMyInfo();

}
