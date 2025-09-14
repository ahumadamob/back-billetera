package com.ahumadamob.fnanz.dto.response;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objeto de transferencia que representa una respuesta de error de la API.
 * Contiene una lista de mensajes de error por campo y una marca de tiempo
 * que indica cuándo se produjo el error.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseErrorDto {

    /**
     * Colección de mensajes que describen errores para campos específicos.
     */
    private List<Message> messages;

    /**
     * Momento en el que se creó la respuesta de error.
     */
    @Builder.Default
    private Instant timestamp = Instant.now();

    /**
     * Representa el error de un campo.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String field;
        private String message;
    }
}

