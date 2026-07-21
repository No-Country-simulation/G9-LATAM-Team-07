package com.techmind.api.controller;

import com.techmind.api.dto.ContenidoRequest;
import com.techmind.api.dto.ContenidoResponse;
import com.techmind.api.service.ClasificacionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contenido")
public class ContenidoController {

    private final ClasificacionService clasificacionService;

    public ContenidoController(ClasificacionService clasificacionService) {
        this.clasificacionService = clasificacionService;
    }

    @PostMapping
    public ResponseEntity<ContenidoResponse> procesarContenido(@Valid @RequestBody ContenidoRequest request) {
        ContenidoResponse response = clasificacionService.clasificarTexto(request);
        return ResponseEntity.ok(response);
    }
}