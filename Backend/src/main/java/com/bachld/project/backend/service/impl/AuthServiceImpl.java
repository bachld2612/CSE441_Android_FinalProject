package com.bachld.project.backend.service.impl;

import com.bachld.project.backend.dto.request.auth.IntrospectRequest;
import com.bachld.project.backend.dto.request.auth.LoginRequest;
import com.bachld.project.backend.dto.request.auth.LogoutRequest;
import com.bachld.project.backend.dto.response.auth.InfoResponse;
import com.bachld.project.backend.dto.response.auth.IntrospectResponse;
import com.bachld.project.backend.dto.response.auth.LoginResponse;
import com.bachld.project.backend.entity.GiangVien;
import com.bachld.project.backend.entity.InvalidateToken;
import com.bachld.project.backend.entity.SinhVien;
import com.bachld.project.backend.entity.TaiKhoan;
import com.bachld.project.backend.enums.Role;
import com.bachld.project.backend.exception.ApplicationException;
import com.bachld.project.backend.exception.ErrorCode;
import com.bachld.project.backend.mapper.AuthMapper;
import com.bachld.project.backend.repository.InvalidateTokenRepository;
import com.bachld.project.backend.repository.TaiKhoanRepository;
import com.bachld.project.backend.service.AuthService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    TaiKhoanRepository taiKhoanRepository;
    InvalidateTokenRepository invalidateTokenRepository;
    private final AuthMapper authMapper;
    @NonFinal
    @Value("${jwt.signerKey}")
    String SIGNER_KEY;
    PasswordEncoder passwordEncoder;


    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        TaiKhoan taiKhoan = taiKhoanRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ApplicationException(ErrorCode.ACCOUNT_NOT_FOUND));
        if(!passwordEncoder.matches(loginRequest.getMatKhau(), taiKhoan.getMatKhau())) {
            throw new ApplicationException(ErrorCode.WRONG_PASSWORD);
        }
        if(taiKhoan.getVaiTro() == Role.SINH_VIEN){
            SinhVien sinhVien = taiKhoan.getSinhVien();
            if (!sinhVien.isKichHoat()){
                throw new ApplicationException(ErrorCode.INACTIVATED_ACCOUNT);
            }
        }
        return LoginResponse.builder()
                .token(generateToken(taiKhoan))
                .build();
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest request) {
        boolean isValid = true;
        try {
            verifyToken(request.getToken());
        }catch (Exception e){
            isValid = false;
        }
        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    @Override
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        SignedJWT signedJWT = verifyToken(request.getToken());
        String tokenId = signedJWT.getJWTClaimsSet().getJWTID();
        Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
        InvalidateToken invalidateToken = InvalidateToken.builder()
                .tokenId(tokenId)
                .expiryTime(expiration)
                .build();
        invalidateTokenRepository.save(invalidateToken);
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public InfoResponse getMyInfo() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        TaiKhoan taiKhoan = taiKhoanRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ApplicationException(ErrorCode.ACCOUNT_NOT_FOUND));
        InfoResponse infoResponse = null;
        if(taiKhoan.getVaiTro() == Role.SINH_VIEN){
            SinhVien sinhVien = taiKhoan.getSinhVien();
            infoResponse = InfoResponse.builder()
                    .maSV(sinhVien.getMaSV())
                    .hoTen(sinhVien.getHoTen())
                    .email(authentication.getName())
                    .soDienThoai(sinhVien.getSoDienThoai())
                    .lop(sinhVien.getLop().getTenLop())
                    .nganh(sinhVien.getLop().getNganh().getTenNganh())
                    .khoa(sinhVien.getLop().getNganh().getKhoa().getTenKhoa())
                    .build();
        }else if(taiKhoan.getVaiTro() != Role.ADMIN){
            GiangVien giangVien = taiKhoan.getGiangVien();
            infoResponse = InfoResponse.builder()
                    .maGV(giangVien.getMaGV())
                    .email(authentication.getName())
                    .hoTen(giangVien.getHoTen())
                    .hocVi(giangVien.getHocVi())
                    .hocHam(giangVien.getHocHam())
                    .soDienThoai(giangVien.getSoDienThoai())
                    .boMon(giangVien.getBoMon().getTenBoMon())
                    .khoa(giangVien.getBoMon().getKhoa().getTenKhoa())
                    .build();
        }else {
            infoResponse = InfoResponse.builder()
                    .email(authentication.getName())
                    .build();
        }
        return infoResponse;
    }

    private String generateToken(TaiKhoan request){
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(request.getEmail())
                .issuer("bachld")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(3, ChronoUnit.HOURS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", request.getVaiTro())
                .build();
        Payload payload = new Payload(claims.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY));
            return jwsObject.serialize();
        }catch (JOSEException e) {
            log.error("Cannot sign JWT", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        boolean verified = signedJWT.verify(verifier);
        Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
        if(!(verified && expiration.after(new Date()))){
            throw new ApplicationException(ErrorCode.UNAUTHENTICATED);
        }
        if (invalidateTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())){
            throw new ApplicationException(ErrorCode.UNAUTHENTICATED);
        }
        return signedJWT;

    }
}
