package com.example.huertohogar_bd.controller;

import com.example.huertohogar_bd.entity.Usuario;
import com.example.huertohogar_bd.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // Crear usuario desde Admin (opcional)
    @PostMapping
    public Usuario crearUsuario(@RequestBody Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    // Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        usuarioRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Actualizar usuario completo (Nombre, Email, Rol)
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario detalles) {
         return usuarioRepository.findById(id).map(u -> {
             // Solo actualizamos si el dato viene en la petición (no es null)
             if (detalles.getNombre() != null) u.setNombre(detalles.getNombre());
             if (detalles.getEmail() != null) u.setEmail(detalles.getEmail());
             if (detalles.getRol() != null) u.setRol(detalles.getRol());
             
             return ResponseEntity.ok(usuarioRepository.save(u));
         }).orElse(ResponseEntity.notFound().build());
    }
}