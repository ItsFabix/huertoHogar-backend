package com.example.huertohogar_bd.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "productos")
@Data
public class Producto {

    @Id
    private String codigo; // Ejemplo: "FR001"

    private String nombre;
    private String categoria;
    private Integer precio;
    private Integer stock;
    private String descripcion;
    private Boolean oferta;
    private Integer precioOferta;

    @Column(columnDefinition = "LONGTEXT") // Agrega esta anotación
    private String imagen;
}