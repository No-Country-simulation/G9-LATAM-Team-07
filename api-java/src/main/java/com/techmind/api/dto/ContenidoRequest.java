package com.techmind.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContenidoRequest(
    @NotBlank(message = "El título es obligatorio") 
    String titulo,
    
    @NotBlank(message = "El texto no puede estar vacío")
    @Size(min = 10, message = "El texto debe tener al menos 10 caracteres") 
    String texto
) {}