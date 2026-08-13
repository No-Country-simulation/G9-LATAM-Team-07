package com.techmind.api.service;

import com.techmind.api.dto.ContenidoRequest;
import com.techmind.api.dto.ContenidoResponse;
import com.techmind.api.model.Contenido;
import com.techmind.api.repository.ContenidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClasificacionService {

    private final ContenidoRepository contenidoRepository;
    private final boolean useMock = true;

    public ClasificacionService(ContenidoRepository contenidoRepository) {
        this.contenidoRepository = contenidoRepository;
    }

    public Contenido guardarContenido(String titulo, String texto, String categoria, Double probabilidad, String keywords) {
        Contenido contenido = new Contenido(titulo, texto, categoria, probabilidad, keywords);
        return contenidoRepository.save(contenido);
    }

    public List<Contenido> listarPorCategoria(String categoria) {
        if (categoria != null && !categoria.isBlank()) {
            return contenidoRepository.findByCategoriaIgnoreCase(categoria);
        }
        return contenidoRepository.findAll();
    }

    public ContenidoResponse clasificarTexto(ContenidoRequest request) {
        ContenidoResponse response;

        if (useMock) {
            String tituloLower = request.titulo().toLowerCase();
            String textoLower = request.texto().toLowerCase();

            if (tituloLower.contains("react") || tituloLower.contains("next") || 
                textoLower.contains("frontend") || textoLower.contains("css") || 
                textoLower.contains("tailwind") || textoLower.contains("redux") ||
                textoLower.contains("state")) {
                
                response = new ContenidoResponse(
                        "Frontend",
                        0.94,
                        List.of("React", "Next.js", "TailwindCSS")
                );
            }
            else if (tituloLower.contains("datos") || tituloLower.contains("pandas") || 
                textoLower.contains("python") || textoLower.contains("machine learning") || 
                textoLower.contains("ia")) {
                
                response = new ContenidoResponse(
                        "Data Science",
                        0.91,
                        List.of("Python", "Pandas", "Jupyter Notebook")
                );
            }
            else {
                response = new ContenidoResponse(
                        "Backend",
                        0.89,
                        List.of("Java", "Spring Boot", "API REST")
                );
            }
        } else {
            response = new ContenidoResponse(
                    "Backend",
                    0.89,
                    List.of("Java", "Spring Boot", "API REST")
            );
        }

        String keywordsStr = (response.informacion_adicional() != null) 
                ? String.join(", ", response.informacion_adicional()) 
                : "";

        guardarContenido(
                request.titulo(),
                request.texto(),
                response.categoria(),
                response.probabilidad(),
                keywordsStr
        );

        return response;
    }
}