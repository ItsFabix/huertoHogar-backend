package com.example.huertohogar_bd.repository;

import com.example.huertohogar_bd.entity.Boleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BoletaRepository extends JpaRepository<Boleta, Long> {
    // Para buscar boletas de un usuario específico (historial)
    List<Boleta> findByUsuarioEmail(String email);
}