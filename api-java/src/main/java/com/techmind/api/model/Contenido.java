package com.techmind.api.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contenidos")
public class Contenido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Column(length = 4000) // Soporta textos largos de contenido
    private String texto;

    private String categoria;

    private Double probabilidad;

    private String informacionAdicional; // Guarda las palabras clave como String separado por comas

    private LocalDateTime fechaRegistro;

    public Contenido() {}

    public Contenido(String titulo, String texto, String categoria, Double probabilidad, String informacionAdicional) {
        this.titulo = titulo;
        this.texto = texto;
        this.categoria = categoria;
        this.probabilidad = probabilidad;
        this.informacionAdicional = informacionAdicional;
        this.fechaRegistro = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public Double getProbabilidad() { return probabilidad; }
    public void setProbabilidad(Double probabilidad) { this.probabilidad = probabilidad; }
    public String getInformacionAdicional() { return informacionAdicional; }
    public void setInformacionAdicional(String informacionAdicional) { this.informacionAdicional = informacionAdicional; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
}