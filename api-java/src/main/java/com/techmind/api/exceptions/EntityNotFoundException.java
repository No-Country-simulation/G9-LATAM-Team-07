package com.techmind.api.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String mensaje) {
        super(mensaje);
    }

    
}
