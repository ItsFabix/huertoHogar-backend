package com.example.huertohogar_bd.entity;

import jakarta.persistence.*;
import lombok.Data;
// Importante: Spring Security usa UserDetails, pero por simplicidad 
// primero creamos la entidad base y luego la configuramos.

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    private String rol; // 'admin', 'cliente', etc.
    private String rut;
}