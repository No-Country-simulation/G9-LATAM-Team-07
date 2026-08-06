package com.techmind.api.exceptions;

public class ServicioInferenciaException extends RuntimeException {

    public ServicioInferenciaException(String mensaje) {
        super(mensaje);
    }

    public ServicioInferenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
