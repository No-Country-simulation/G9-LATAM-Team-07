package com.techmind.api.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

public record ContenidoRequest(
        @NotBlank(message = "El título es obligatorio")
        String titulo,

        @NotBlank(message = "El texto no puede estar vacío")
        String texto
){
        @AssertTrue(message = "El contenido combinado de 'titulo' y 'texto' debe tener al menos 10 caracteres")
        public boolean isLongitudMinimaValida() {
                if (titulo == null || texto == null) {
                        return false;
                }
                return (titulo.trim() + " " + texto.trim()).length() >= 10;
        }
}
