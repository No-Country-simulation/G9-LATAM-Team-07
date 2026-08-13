package com.techmind.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Contenido técnico que se quiere clasificar")
public record ContenidoRequest(
        @Schema(
                description = "Título del contenido",
                example = "Payment service deployment runbook"
        )
        @NotBlank(message = "El título es obligatorio")
        String titulo,

        @Schema(
                description = "Texto a clasificar. Junto con el título debe sumar al menos 10 caracteres",
                example = "Steps to roll out the payment service: build the docker image and run the pipeline."
        )
        @NotBlank(message = "El texto no puede estar vacío")
        String texto
){
        @Schema(hidden = true)
        @AssertTrue(message = "El contenido combinado de 'titulo' y 'texto' debe tener al menos 10 caracteres")
        public boolean isLongitudMinimaValida() {
                if (titulo == null || texto == null) {
                        return false;
                }
                return (titulo.trim() + " " + texto.trim()).length() >= 10;
        }
}
