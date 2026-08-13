package com.techmind.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Resultado de la clasificación")
public record ContenidoResponse(

    @Schema(description = "Categoría detectada por el modelo", example = "DevOps")
    String categoria,

    @Schema(description = "Confianza de la predicción, de 0 a 1", example = "0.86")
    Double probabilidad,

    @Schema(description = "Palabras clave extraídas del texto", example = "[\"payment\", \"docker\", \"pipeline\"]")
    List<String> informacion_adicional

) {}
