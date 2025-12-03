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

    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("El email ya existe");
        }
        // Encriptamos la contraseña antes de guardar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        // Asignamos rol por defecto si no viene
        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            usuario.setRol("cliente");
        }
        Usuario nuevoUsuario = usuarioRepository.save(usuario);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @PostMapping("/login")
    public ResponseEntity<?> crearToken(@RequestBody LoginRequest request) throws Exception {
        try {
            // Esto verifica email y contraseña automáticamente
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            throw new Exception("Credenciales incorrectas", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        
        // Obtenemos el rol del usuario (asumiendo que tiene uno solo para simplificar)
        String rol = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        
        final String jwt = jwtUtil.generateToken(userDetails.getUsername(), rol);

        return ResponseEntity.ok(new LoginResponse(jwt));
    }
}