package com.techmind.api.controller;

import com.techmind.api.dto.ContenidoRequest;
import com.techmind.api.dto.ContenidoResponse;
import com.techmind.api.dto.PythonResponse;
import com.techmind.api.exceptions.ServicioInferenciaException;
import com.techmind.api.model.Contenido;
import com.techmind.api.service.ClasificacionService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @GetMapping
    public ResponseEntity<List<Contenido>> listarContenidos(@RequestParam(required = false) String categoria) {
        List<Contenido> lista = clasificacionService.listarPorCategoria(categoria);
        return ResponseEntity.ok(lista);
    }
}
