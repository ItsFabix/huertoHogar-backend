package com.example.huertohogar_bd.controller;

import com.example.huertohogar_bd.dto.LoginRequest;
import com.example.huertohogar_bd.dto.LoginResponse;
import com.example.huertohogar_bd.entity.Usuario;
import com.example.huertohogar_bd.repository.UsuarioRepository;
import com.example.huertohogar_bd.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Endpoint para Registro
    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("El email ya existe");
        }
        // Encriptar contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        
        // Asignar rol por defecto si no viene
        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            usuario.setRol("cliente");
        }
        
        Usuario nuevoUsuario = usuarioRepository.save(usuario);
        return ResponseEntity.ok(nuevoUsuario);
    }

    // Endpoint para Login
    @PostMapping("/login")
    public ResponseEntity<?> crearToken(@RequestBody LoginRequest request) throws Exception {
        try {
            // Validar credenciales con Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            throw new Exception("Credenciales incorrectas", e);
        }

        // Cargar detalles del usuario para el token
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        
        // Buscar el usuario completo en la BD para obtener sus datos extra (nombre, rol)
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new Exception("Usuario no encontrado en BD"));
        
        // Generar el token JWT
        final String jwt = jwtUtil.generateToken(userDetails.getUsername(), usuario.getRol());

        // IMPORTANTE: Devolvemos el token Y los datos del usuario (Nombre, Email, Rol)
        // Esto permite que el Frontend sepa quién se logueó
        return ResponseEntity.ok(new LoginResponse(jwt, usuario.getNombre(), usuario.getEmail(), usuario.getRol()));
    }
}