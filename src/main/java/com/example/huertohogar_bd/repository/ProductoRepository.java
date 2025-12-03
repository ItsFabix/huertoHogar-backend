package com.example.huertohogar_bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.huertohogar_bd.entity.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, String> {
    // JpaRepository ya incluye findAll(), save(), findById(), etc.
}