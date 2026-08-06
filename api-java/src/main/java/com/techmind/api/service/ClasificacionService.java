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

    public ClasificacionService(ContenidoRepository contenidoRepository) {
        this.contenidoRepository = contenidoRepository;
    }

    /**
     * Persiste en la base de datos el contenido junto con la prediccion realizada por la IA.
     */
    public Contenido guardarContenido(String titulo, String texto, String categoria, Double probabilidad) {
        Contenido contenido = new Contenido();
        contenido.setTitulo(titulo);
        contenido.setTexto(texto);
        contenido.setCategoria(categoria);
        contenido.setProbabilidad(probabilidad);

        return contenidoRepository.save(contenido);
    }

    /**
     * Retorna los contenidos filtrados por categoria (ignorando mayusculas/minusculas).
     * Si 'categoria' es nula o esta vacia, retorna todos los contenidos almacenados.
     */
    public List<Contenido> listarPorCategoria(String categoria) {
        if (categoria != null && !categoria.isBlank()) {
            return contenidoRepository.findByCategoriaIgnoreCase(categoria);
        }
        return contenidoRepository.findAll();
    }

    public ContenidoResponse clasificarTexto(ContenidoRequest request) {
        // Fallback por defecto si Python no está disponible
        return new ContenidoResponse(
                "Backend",
                0.89,
                List.of("Java", "Spring Boot", "API REST")
        );
    }
}
