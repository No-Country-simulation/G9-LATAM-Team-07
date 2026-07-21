package com.techmind.api.service;

import com.techmind.api.dto.ContenidoRequest;
import com.techmind.api.dto.ContenidoResponse;
import com.techmind.api.dto.PythonResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.List;

@Service
public class ClasificacionService {

    private final RestClient restClient;
    // cambiar a false al integrar con Python.
    private final boolean useMock = true; 

    public ClasificacionService() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8000") // endpoint futuro del servicio Python (HU-07)
                .build();
    }

    public ContenidoResponse clasificarTexto(ContenidoRequest request) {
        if (useMock) {
            // simulacion del mock
            return new ContenidoResponse("Backend", 0.89, List.of("Java", "Spring Boot", "API REST"));
        }

        try {
            // llamada HTTP
            PythonResponse pythonRaw = restClient.post()
                    .uri("/predict")
                    .body(request)
                    .retrieve()
                    .body(PythonResponse.class);

            // mapeo y transformacion de la respuesta del modelo al formato requerido
            return new ContenidoResponse(
                    pythonRaw.label(),
                    pythonRaw.confidence(),
                    pythonRaw.keywords()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con el servicio de inferencia de IA");
        }
    }
}