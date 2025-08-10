package com.bachld.project.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorCode {

    UNAUTHORIZED(1001, "Unauthorized", HttpStatus.UNAUTHORIZED),
    UNAUTHENTICATED(1002, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    INVALID_VALIDATION(1003,"Invalid Validation", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1004, "Invalid Email", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1005, "Password must be at least 6 characters", HttpStatus.BAD_REQUEST),
    ACCOUNT_NOT_FOUND(1006, "Account not found", HttpStatus.NOT_FOUND),
    WRONG_PASSWORD(1007, "Wrong Password", HttpStatus.FORBIDDEN),
    INACTIVATED_ACCOUNT(1008, "Inactivated Account", HttpStatus.FORBIDDEN),
    KHOA_NOT_FOUND(1009, "Khoa Not Found", HttpStatus.NOT_FOUND),
    KHOA_EMPTY(1010, "Ten khoa cannot be empty", HttpStatus.BAD_REQUEST),
    DUPLICATED_KHOA(1011, "Ten khoa already exists", HttpStatus.BAD_REQUEST),
    NGANH_EMPTY(1012, "Ten nganh cannot be empty", HttpStatus.BAD_REQUEST),
    DUPLICATED_NGANH(1013, "Ten nganh already exists", HttpStatus.BAD_REQUEST),
    NGANH_NOT_FOUND(1014, "Ten nganh not found", HttpStatus.NOT_FOUND),
    DUPLICATED_BO_MON(1015, "Ten bo mon already exists", HttpStatus.BAD_REQUEST),
    BO_MON_EMPTY(1016, "Ten bo cannot be empty", HttpStatus.BAD_REQUEST),
    BO_MON_NOT_FOUND(1017, "Bo mon not found", HttpStatus.BAD_REQUEST),
    LOP_EMPTY(1018, "Ten lop cannot be empty", HttpStatus.BAD_REQUEST),
    DUPLICATED_LOP(1019, "Ten lop already exists", HttpStatus.BAD_REQUEST),
    LOP_NOT_FOUND(1020, "Lop not found", HttpStatus.NOT_FOUND),

    //Duc
    DE_TAI_EMPTY(1101, "Ten de tai cannot be empty", HttpStatus.BAD_REQUEST),
    DE_TAI_GVHD_REQUIRED(1102, "GVHD is required", HttpStatus.BAD_REQUEST),
    DE_TAI_FILE_INVALID(1103, "Invalid overview file", HttpStatus.BAD_REQUEST),
    GIANG_VIEN_NOT_FOUND(1104, "Giang vien not found", HttpStatus.NOT_FOUND),
    SINH_VIEN_NOT_FOUND(1105, "Sinh vien not found", HttpStatus.NOT_FOUND),
    SINH_VIEN_ALREADY_REGISTERED_DE_TAI(1106, "Sinh vien already registered a de tai", HttpStatus.BAD_REQUEST),
    UPLOAD_FILE_FAILED(1107, "Upload file failed", HttpStatus.INTERNAL_SERVER_ERROR),
    DE_TAI_NOT_FOUND(1108, "De tai not found", HttpStatus.NOT_FOUND),
    TRANG_THAI_INVALID(1109, "Trang thai invalid", HttpStatus.BAD_REQUEST),
    NOT_GVHD_OF_DE_TAI(1110, "Giang vien khong co quyen tren de tai nay", HttpStatus.FORBIDDEN),
    DE_TAI_NOT_IN_PENDING_STATUS(1111, "Only process de tai in PENDING status", HttpStatus.BAD_REQUEST),


    ;
    int code;
    String message;
    HttpStatusCode httpStatusCode;

}


