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
    // mantenemos useMock en true para demo y pruebas locales
    private final boolean useMock = true;

    public ClasificacionService() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8000") // endpoint futuro en OCI
                .build();
    }

    public ContenidoResponse clasificarTexto(ContenidoRequest request) {
        if (useMock) {
            String tituloLower = request.titulo().toLowerCase();
            String textoLower = request.texto().toLowerCase();

            // 1. Detección para FRONTEND
            if (tituloLower.contains("react") || tituloLower.contains("next") || 
                textoLower.contains("frontend") || textoLower.contains("css") || 
                textoLower.contains("tailwind") || tituloLower.contains("componentes")) {
                
                return new ContenidoResponse(
                        "Frontend",
                        0.94,
                        List.of("React", "Next.js", "TailwindCSS")
                );
            }

            // 2. Detección para DATA SCIENCE
            if (tituloLower.contains("datos") || tituloLower.contains("pandas") || 
                textoLower.contains("python") || textoLower.contains("machine learning") || 
                textoLower.contains("exploratorio") || tituloLower.contains("ia")) {
                
                return new ContenidoResponse(
                        "Data Science",
                        0.91,
                        List.of("Python", "Pandas", "Jupyter Notebook")
                );
            }

            // 3. Respuesta por defecto: BACKEND
            return new ContenidoResponse(
                    "Backend",
                    0.89,
                    List.of("Java", "Spring Boot", "API REST")
            );
        }

        try {
            // Llamada HTTP real hacia microservicio Python
            PythonResponse pythonRaw = restClient.post()
                    .uri("/predict")
                    .body(request)
                    .retrieve()
                    .body(PythonResponse.class);

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