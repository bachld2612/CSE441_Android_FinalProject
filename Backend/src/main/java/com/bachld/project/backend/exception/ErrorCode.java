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
    FILE_NOT_FOUND(1201, "File not found", HttpStatus.NOT_FOUND),
    FILE_URL_EMPTY(1202, "File URL cannot be empty", HttpStatus.BAD_REQUEST),
    DE_TAI_ID_EMPTY(1203, "De tai ID cannot be empty", HttpStatus.BAD_REQUEST),
    DE_TAI_NOT_FOUND   (1204, "De tai not found",   HttpStatus.NOT_FOUND),
    DE_CUONG_NOT_FOUND (1205, "De cuong not found", HttpStatus.NOT_FOUND),
    DE_CUONG_ALREADY_APPROVED(1206, "De cuong already approved!", HttpStatus.BAD_REQUEST),
    DE_CUONG_ALREADY_SUBMITTED(1207, "De cuong already submitted!", HttpStatus.BAD_REQUEST),
    DE_CUONG_ALREADY_REJECTED(1208, "De cuong already rejected!", HttpStatus.BAD_REQUEST),
    DE_CUONG_EMPTY(1209, "De cuong cannot be empty", HttpStatus.CONFLICT),
    OUTLINE_NOT_PENDING(1210, "Outline is not in PENDING state", HttpStatus.CONFLICT),
    DE_TAI_ID_MUST_BE_POSITIVE(1211, "De tai ID must be positive", HttpStatus.BAD_REQUEST),
    DE_TAI_NOT_ACCEPTED       (1212, "Topic has not been accepted", HttpStatus.BAD_REQUEST),
    DE_CUONG_REASON_REQUIRED(1213, "Reason is required when rejecting the outline", HttpStatus.BAD_REQUEST),
    ;
    int code;
    String message;
    HttpStatusCode httpStatusCode;

}


