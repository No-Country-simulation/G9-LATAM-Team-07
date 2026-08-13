package com.techmind.api.controller;

import com.techmind.api.dto.ContenidoRequest;
import com.techmind.api.dto.ContenidoResponse;
import com.techmind.api.dto.PythonResponse;
import com.techmind.api.exceptions.ServicioInferenciaException;
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
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Contenido", description = "Clasificación y consulta de contenido técnico")
@RestController
@RequestMapping("/contenido")
public class ContenidoController {

    private final ClasificacionService clasificacionService;
    private final RestTemplate restTemplate;

    private static final String PYTHON_SERVICE_URL = "http://predict-service:8000/contenido";

    public ContenidoController(ClasificacionService clasificacionService, RestTemplate restTemplate) {
        this.clasificacionService = clasificacionService;
        this.restTemplate = restTemplate;
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
        try {
            // 1. Cabeceras JSON explícitas
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 2. Cuerpo enviado a Python
            Map<String, String> body = new HashMap<>();
            body.put("titulo", request.titulo());
            body.put("texto", request.texto());

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

            // 3. Consumir la API de Python
            PythonResponse pythonResponse = restTemplate.postForObject(
                    PYTHON_SERVICE_URL,
                    entity,
                    PythonResponse.class
            );

            // 4. Mapear y responder
            if (pythonResponse != null && pythonResponse.label() != null) {

                //Guardar el contenido en la BD mediante el servicio
                Contenido contenidoGuardado = clasificacionService.guardarContenido(
                        request.titulo(),
                        request.texto(),
                        pythonResponse.label(),
                        pythonResponse.confidence()
                );

                ContenidoResponse response = new ContenidoResponse(
                        pythonResponse.label(),
                        pythonResponse.confidence(),
                        pythonResponse.keywords()
                );

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                throw new ServicioInferenciaException("El servicio de Python devolvió una respuesta nula o incompleta");
            }

        } catch (RestClientException e) {
            throw new ServicioInferenciaException("Error al comunicarse con el servicio de clasificación en Python", e);
        }
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
