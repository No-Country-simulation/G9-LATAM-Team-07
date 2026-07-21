package com.techmind.api.dto;

import java.util.List;

public record PythonResponse(
    String label,
    Double confidence,
    List<String> keywords
) {}