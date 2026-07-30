package com.techmind.api.repository;

import com.techmind.api.model.Contenido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContenidoRepository extends JpaRepository<Contenido, Long> {
    List<Contenido> findByCategoriaIgnoreCase(String categoria);
}