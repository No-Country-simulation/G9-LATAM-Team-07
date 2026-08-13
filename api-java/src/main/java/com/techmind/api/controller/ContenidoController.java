package com.techmind.api.controller;

import com.techmind.api.dto.ContenidoRequest;
import com.techmind.api.dto.ContenidoResponse;
import com.techmind.api.model.Contenido;
import com.techmind.api.service.ClasificacionService;
import com.techmind.api.exceptions.GestorDeErrores;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Contenido", description = "Clasificación y consulta de contenido técnico")
@RestController
@RequestMapping("/contenido")
@CrossOrigin(origins = "*") // Habilita las peticiones HTTP desde React
public class ContenidoController {

    private final ClasificacionService clasificacionService;

    public ContenidoController(ClasificacionService clasificacionService) {
        this.clasificacionService = clasificacionService;
    }

    // Endpoint de Clasificación y Guardado (POST /contenido)
    @Operation(
            summary = "Clasifica un contenido y lo guarda",
            description = "Envía el texto al servicio de clasificación, guarda el resultado y devuelve la categoría con su probabilidad."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contenido clasificado y guardado"),
            @ApiResponse(responseCode = "400", description = "Faltan datos o no cumplen las validaciones",
                    content = @Content(schema = @Schema(implementation = GestorDeErrores.DatosError.class))),
            @ApiResponse(responseCode = "503", description = "El servicio de clasificación no responde",
                    content = @Content(schema = @Schema(implementation = GestorDeErrores.DatosError.class)))
    })
    @PostMapping
    public ResponseEntity<ContenidoResponse> procesarContenido(@Valid @RequestBody ContenidoRequest request) {
        // La clasificación y la persistencia en H2 se resuelven limpiamente en el Service
        ContenidoResponse response = clasificacionService.clasificarTexto(request);
        return ResponseEntity.ok(response);
    }

    // Endpoint de Consulta por Categoría (GET /contenido)
    @Operation(
            summary = "Lista los contenidos guardados",
            description = "Sin el parámetro categoria devuelve todos los contenidos."
    )
    @ApiResponse(responseCode = "200", description = "Listado de contenidos")
    @GetMapping
    public ResponseEntity<List<Contenido>> listarContenidos(
            @Parameter(description = "Filtra por categoría exacta", example = "DevOps")
            @RequestParam(required = false) String categoria) {
        List<Contenido> lista = clasificacionService.listarPorCategoria(categoria);
        return ResponseEntity.ok(lista);
    }
}