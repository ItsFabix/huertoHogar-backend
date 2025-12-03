package com.example.huertohogar_bd.controller;

import com.example.huertohogar_bd.dto.CompraRequest;
import com.example.huertohogar_bd.entity.*;
import com.example.huertohogar_bd.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/boletas")
@CrossOrigin(origins = "*")
public class BoletaController {

    @Autowired
    private BoletaRepository boletaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    @Transactional 
    public ResponseEntity<?> crearBoleta(@RequestBody CompraRequest compra) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuario = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Boleta nuevaBoleta = new Boleta();
        nuevaBoleta.setUsuario(usuario);
        nuevaBoleta.setFecha(LocalDateTime.now());
        nuevaBoleta.setEstado("pagada");
        nuevaBoleta.setFolio(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        
        // GUARDAR LA DIRECCIÓN
        nuevaBoleta.setDireccionEnvio(compra.getDireccionEnvio());

        List<DetalleBoleta> detalles = new ArrayList<>();
        int totalCalculado = 0;

        for (CompraRequest.ItemPedido item : compra.getItems()) {
            Producto prod = productoRepository.findById(item.getCodigo())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.getCodigo()));

            if (prod.getStock() < item.getCantidad()) {
                return ResponseEntity.badRequest().body("No hay stock suficiente para: " + prod.getNombre());
            }
            
            prod.setStock(prod.getStock() - item.getCantidad());
            productoRepository.save(prod);

            DetalleBoleta detalle = new DetalleBoleta();
            detalle.setBoleta(nuevaBoleta);
            detalle.setProducto(prod);
            detalle.setCantidad(item.getCantidad());
            
            int precioFinal = (prod.getOferta() != null && prod.getOferta() && prod.getPrecioOferta() > 0) 
                              ? prod.getPrecioOferta() 
                              : prod.getPrecio();
            
            detalle.setPrecioUnitario(precioFinal);
            
            totalCalculado += (precioFinal * item.getCantidad());
            detalles.add(detalle);
        }

        nuevaBoleta.setTotal(totalCalculado);
        nuevaBoleta.setDetalles(detalles);

        boletaRepository.save(nuevaBoleta);

        return ResponseEntity.ok(nuevaBoleta);
    }

    @GetMapping("/mis-compras")
    public List<Boleta> obtenerMisCompras() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return boletaRepository.findByUsuarioEmail(auth.getName());
    }

    @GetMapping
    public List<Boleta> listarTodasLasBoletas() {
        return boletaRepository.findAll();
    }
    
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarBoleta(@PathVariable Long id) {
        return boletaRepository.findById(id)
                .map(boleta -> {
                    boleta.setEstado("cancelada");
                    return ResponseEntity.ok(boletaRepository.save(boleta));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Boleta> obtenerBoletaPorId(@PathVariable Long id) {
        return boletaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}