package com.example.huertohogar_bd.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "boletas")
@Data
public class Boleta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String folio;
    private LocalDateTime fecha;
    private Integer total;
    private String estado;

    @Column(length = 500) // Permitir textos largos
    private String direccionEnvio; // <--- AGREGAR ESTO

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Relación: Una boleta tiene muchos detalles
    // CascadeType.ALL permite guardar la boleta y que sus detalles se guarden solos
    @OneToMany(mappedBy = "boleta", cascade = CascadeType.ALL)
    private List<DetalleBoleta> detalles;
}