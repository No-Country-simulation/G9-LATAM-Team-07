package com.techmind.api.controller;

import com.techmind.api.dto.ContenidoRequest;
import com.techmind.api.dto.ContenidoResponse;
import com.techmind.api.dto.PythonResponse;
import com.techmind.api.model.Contenido;
import com.techmind.api.service.ClasificacionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contenido")
public class ContenidoController {

    private final ClasificacionService clasificacionService;
    private final RestTemplate restTemplate;

    // URL interna del contenedor de Python dentro de Docker
    private static final String PYTHON_SERVICE_URL = "http://python_predict_api:8000/contenido";

    public ContenidoController(ClasificacionService clasificacionService, RestTemplate restTemplate) {
        this.clasificacionService = clasificacionService;
        this.restTemplate = restTemplate;
    }

    // Endpoint de Clasificación y Guardado (POST /contenido)
    @PostMapping
    public ResponseEntity<ContenidoResponse> procesarContenido(@Valid @RequestBody ContenidoRequest request) {
        try {
            // 1. Armar el JSON con titulo y texto
            Map<String, String> body = new HashMap<>();
            body.put("titulo", request.titulo());
            body.put("texto", request.texto());

            // 2. Consumir el microservicio de Python
            PythonResponse pythonResponse = restTemplate.postForObject(
                PYTHON_SERVICE_URL, 
                body, 
                PythonResponse.class
            );

            // 3. Mapear la respuesta real de Python si la llamada fue exitosa
            if (pythonResponse != null) {
                ContenidoResponse response = new ContenidoResponse(
                    pythonResponse.label(),
                    pythonResponse.confidence(),
                    pythonResponse.keywords()
                );
                return ResponseEntity.ok(response);
            }

        } catch (Exception e) {
            System.err.println("Error al conectar con el servicio de Python: " + e.getMessage());
        }

        // Fallback en caso de que el servicio de Python no responda
        ContenidoResponse fallbackResponse = clasificacionService.clasificarTexto(request);
        return ResponseEntity.ok(fallbackResponse);
    }

    // Endpoint de Consulta por Categoría (GET /contenido o GET /contenido?categoria=Backend)
    @GetMapping
    public ResponseEntity<List<Contenido>> listarContenidos(@RequestParam(required = false) String categoria) {
        List<Contenido> lista = clasificacionService.listarPorCategoria(categoria);
        return ResponseEntity.ok(lista);
    }
}
