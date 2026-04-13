package com.lahoa.lahoa_be.exception;

import com.lahoa.lahoa_be.dto.response.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Lỗi sai mật khẩu hoặc tài khoản (Spring Security)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadCredentials(BadCredentialsException ex) {
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Xác thực thất bại",
                "Email hoặc mật khẩu không chính xác. Vui lòng thử lại!"
        );
    }

    // Lỗi không có quyền truy cập (Ví dụ Customer cố vào API Admin)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDenied(AccessDeniedException ex) {
        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "Từ chối truy cập",
                "Bạn không có quyền thực hiện hành động này."
        );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(Exception ex) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "Không tìm thấy",
                ex.getMessage()
        );
    }

    // Lỗi Validation (Khi dùng @Valid ở RequestBody)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .getFirst()
                .getDefaultMessage();
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Dữ liệu không hợp lệ",
                errorMessage
        );
    }

    // Lỗi hệ thống chung (Catch-all)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneralException(Exception ex) {
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Lỗi hệ thống",
                ex.getMessage()
        );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnauthorized(UnauthorizedException ex) {
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Lỗi xác thực",
                ex.getMessage()
        );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponseDTO> handleDisabled(DisabledException ex) {
        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "Tài khoản chưa kích hoạt",
                "Tài khoản của bạn hiện đang bị khóa hoặc chưa được xác thực email!"
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(ResourceNotFoundException ex) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "Không tìm thấy dữ liệu",
                ex.getMessage()
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadRequest(BadRequestException ex) {
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Yêu cầu không hợp lệ",
                ex.getMessage()
        );
    }

    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(HttpStatus status, String error, String message) {
        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .build();
        return new ResponseEntity<>(response, status);
    }
}
