package com.ahumadamob.fnanz.error;

import com.ahumadamob.fnanz.dto.response.ApiResponseErrorDto;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * Maneja conflictos de datos devolviendo un error 409.
     */
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ApiResponseErrorDto> handleConflict(ResourceConflictException ex) {
        ApiResponseErrorDto body = ApiResponseErrorDto.builder()
                .messages(List.of(new ApiResponseErrorDto.Message(ex.getField(), ex.getMessage())))
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Maneja errores de validación de bean validation.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponseErrorDto> handleConstraintViolation(ConstraintViolationException ex) {
        List<ApiResponseErrorDto.Message> messages = ex.getConstraintViolations().stream()
                .map(v -> new ApiResponseErrorDto.Message(v.getPropertyPath().toString(), v.getMessage()))
                .collect(Collectors.toList());
        ApiResponseErrorDto body = ApiResponseErrorDto.builder()
                .messages(messages)
                .build();
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }

    /**
     * Maneja errores de validación en cuerpos de solicitudes.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseErrorDto> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<ApiResponseErrorDto.Message> messages = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> new ApiResponseErrorDto.Message(err.getField(), err.getDefaultMessage()))
                .collect(Collectors.toList());
        ApiResponseErrorDto body = ApiResponseErrorDto.builder()
                .messages(messages)
                .build();
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }
}

