package com.techmind.api.service;

import com.techmind.api.dto.ContenidoRequest;
import com.techmind.api.dto.ContenidoResponse;
import com.techmind.api.dto.PythonResponse;
import com.techmind.api.exceptions.ServicioInferenciaException;
import com.techmind.api.model.Contenido;
import com.techmind.api.repository.ContenidoRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class ClasificacionService {

    private final RestClient restClient;
    private final ContenidoRepository contenidoRepository;
    
    // Mantenemos useMock en true para demo y pruebas locales
    private final boolean useMock = true;

    public ClasificacionService(ContenidoRepository contenidoRepository) {
        this.contenidoRepository = contenidoRepository;
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8000") // Endpoint futuro en OCI
                .build();
    }

    public ContenidoResponse clasificarTexto(ContenidoRequest request) {
        ContenidoResponse response;

        if (useMock) {
            String tituloLower = request.titulo().toLowerCase();
            String textoLower = request.texto().toLowerCase();

            // 1. Detección para FRONTEND
            if (tituloLower.contains("react") || tituloLower.contains("next") || 
                textoLower.contains("frontend") || textoLower.contains("css") || 
                textoLower.contains("tailwind") || tituloLower.contains("componentes")) {
                
                response = new ContenidoResponse(
                        "Frontend",
                        0.94,
                        List.of("React", "Next.js", "TailwindCSS")
                );
            }
            // 2. Detección para DATA SCIENCE
            else if (tituloLower.contains("datos") || tituloLower.contains("pandas") || 
                textoLower.contains("python") || textoLower.contains("machine learning") || 
                textoLower.contains("exploratorio") || tituloLower.contains("ia")) {
                
                response = new ContenidoResponse(
                        "Data Science",
                        0.91,
                        List.of("Python", "Pandas", "Jupyter Notebook")
                );
            }
            // 3. Respuesta por defecto: BACKEND
            else {
                response = new ContenidoResponse(
                        "Backend",
                        0.89,
                        List.of("Java", "Spring Boot", "API REST")
                );
            }
        } else {
            try {
                // Llamada HTTP real hacia microservicio Python
                PythonResponse pythonRaw = restClient.post()
                        .uri("/predict")
                        .body(request)
                        .retrieve()
                        .body(PythonResponse.class);

                response = new ContenidoResponse(
                        pythonRaw.label(),
                        pythonRaw.confidence(),
                        pythonRaw.keywords()
                );
            } catch (Exception e) {
                throw new ServicioInferenciaException("Error al conectar con el servicio de inferencia de IA");
            }
        }

        // --- PERSISTENCIA (HU-10) ---
        // Corregido: usando response.informacion_adicional()
        String keywordsStr = (response.informacion_adicional() != null) 
                ? String.join(", ", response.informacion_adicional()) 
                : "";

        Contenido contenido = new Contenido(
                request.titulo(),
                request.texto(),
                response.categoria(),
                response.probabilidad(),
                keywordsStr
        );
        contenidoRepository.save(contenido);

        return response;
    }

    // --- CONSULTA POR CATEGORÍA (HU-10) ---
    public List<Contenido> listarPorCategoria(String categoria) {
        if (categoria != null && !categoria.isBlank()) {
            return contenidoRepository.findByCategoriaIgnoreCase(categoria);
        }
        return contenidoRepository.findAll();
    }
}