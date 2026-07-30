package com.techmind.api.controller;

import com.techmind.api.dto.ContenidoRequest;
import com.techmind.api.dto.ContenidoResponse;
import com.techmind.api.model.Contenido;
import com.techmind.api.service.ClasificacionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contenido")
public class ContenidoController {

    private final ClasificacionService clasificacionService;

    public ContenidoController(ClasificacionService clasificacionService) {
        this.clasificacionService = clasificacionService;
    }

    // Endpoint de Clasificación y Guardado (POST /contenido)
    @PostMapping
    public ResponseEntity<ContenidoResponse> procesarContenido(@Valid @RequestBody ContenidoRequest request) {
        ContenidoResponse response = clasificacionService.clasificarTexto(request);
        return ResponseEntity.ok(response);
    }

    // Endpoint de Consulta por Categoría (GET /contenido o GET /contenido?categoria=Backend)
    @GetMapping
    public ResponseEntity<List<Contenido>> listarContenidos(@RequestParam(required = false) String categoria) {
        List<Contenido> lista = clasificacionService.listarPorCategoria(categoria);
        return ResponseEntity.ok(lista);
    }
}