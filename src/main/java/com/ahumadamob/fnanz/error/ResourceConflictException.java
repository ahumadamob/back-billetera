package com.ahumadamob.fnanz.error;

/**
 * Excepción lanzada cuando un recurso entra en conflicto con otro existente.
 */
public class ResourceConflictException extends RuntimeException {

    private final String field;

    public ResourceConflictException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
