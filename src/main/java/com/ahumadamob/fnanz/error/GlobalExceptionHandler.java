package com.ahumadamob.fnanz.error;

import com.ahumadamob.fnanz.dto.response.ApiResponseErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Captura y transforma las excepciones en respuestas de error estandarizadas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja la ausencia de recursos devolviendo un error 404.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseErrorDto> handleNotFound(ResourceNotFoundException ex) {
        ApiResponseErrorDto body = ApiResponseErrorDto.builder()
                .messages(List.of(new ApiResponseErrorDto.Message("id", ex.getMessage())))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}

