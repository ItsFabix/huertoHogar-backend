package com.example.huertohogar_bd.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore; // <--- 1. ASEGÚRATE DE TENER ESTA IMPORTACIÓN

@Entity
@Table(name = "detalle_boleta")
@Data
public class DetalleBoleta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "boleta_id")
    @JsonIgnore // <--- 2. ESTA ANOTACIÓN ES LA CLAVE PARA EVITAR EL ERROR
    private Boleta boleta;

    @ManyToOne
    @JoinColumn(name = "producto_codigo")
    private Producto producto;

    private Integer cantidad;
    private Integer precioUnitario;
}