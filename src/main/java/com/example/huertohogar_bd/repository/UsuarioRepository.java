package com.example.huertohogar_bd.repository;

import com.example.huertohogar_bd.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Método mágico de JPA para buscar por email
    Optional<Usuario> findByEmail(String email);
}