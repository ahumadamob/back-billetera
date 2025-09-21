package com.ahumadamob.fnanz.error;

/**
 * Excepción lanzada cuando no se encuentra un recurso por su identificador.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException() {
        super("Recurso no encontrado");
    }
}

