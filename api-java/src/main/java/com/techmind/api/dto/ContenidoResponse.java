package com.techmind.api.dto;

import java.util.List;

public record ContenidoResponse(
    String categoria,
    Double probabilidad,
    List<String> informacion_adicional
) {}