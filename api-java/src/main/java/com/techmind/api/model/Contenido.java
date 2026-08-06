package com.techmind.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "contenidos")
public class Contenido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String texto;

    private String categoria;
    private Double probabilidad;

    public Contenido() {}

    public Contenido(String titulo, String texto, String categoria, Double probabilidad) {
        this.titulo = titulo;
        this.texto = texto;
        this.categoria = categoria;
        this.probabilidad = probabilidad;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Double getProbabilidad() { return probabilidad; }
    public void setProbabilidad(Double probabilidad) { this.probabilidad = probabilidad; }
}
