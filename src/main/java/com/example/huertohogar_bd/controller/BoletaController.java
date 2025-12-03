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
    @Transactional // Si algo falla, no guarda nada (rollback)
    public ResponseEntity<?> crearBoleta(@RequestBody CompraRequest compra) {
        // 1. Obtener usuario autenticado desde el Token
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuario = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Crear la Boleta (Cabecera)
        Boleta nuevaBoleta = new Boleta();
        nuevaBoleta.setUsuario(usuario);
        nuevaBoleta.setFecha(LocalDateTime.now());
        nuevaBoleta.setEstado("pagada");
        nuevaBoleta.setFolio(UUID.randomUUID().toString().substring(0, 8).toUpperCase()); // Folio aleatorio simple
        nuevaBoleta.setDireccionEnvio(compra.getDireccionEnvio());

        List<DetalleBoleta> detalles = new ArrayList<>();
        int totalCalculado = 0;

        // 3. Procesar cada producto del carrito
        for (CompraRequest.ItemPedido item : compra.getItems()) {
            Producto prod = productoRepository.findById(item.getCodigo())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.getCodigo()));

            // Verificar stock (Opcional pero recomendado)
            if (prod.getStock() < item.getCantidad()) {
                return ResponseEntity.badRequest().body("No hay stock suficiente para: " + prod.getNombre());
            }
            // Descontar stock
            prod.setStock(prod.getStock() - item.getCantidad());
            productoRepository.save(prod);

            // Crear detalle
            DetalleBoleta detalle = new DetalleBoleta();
            detalle.setBoleta(nuevaBoleta);
            detalle.setProducto(prod);
            detalle.setCantidad(item.getCantidad());
            
            // Usar el precio de oferta si existe, sino el normal
            int precioFinal = (prod.getOferta() != null && prod.getOferta() && prod.getPrecioOferta() > 0) 
                              ? prod.getPrecioOferta() 
                              : prod.getPrecio();
            
            detalle.setPrecioUnitario(precioFinal);
            
            totalCalculado += (precioFinal * item.getCantidad());
            detalles.add(detalle);
        }

        nuevaBoleta.setTotal(totalCalculado);
        nuevaBoleta.setDetalles(detalles);

        // 4. Guardar todo en cascada
        boletaRepository.save(nuevaBoleta);

        return ResponseEntity.ok(nuevaBoleta);
    }

    // Endpoint para ver mis compras (Historial)
    @GetMapping("/mis-compras")
    public List<Boleta> obtenerMisCompras() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return boletaRepository.findByUsuarioEmail(auth.getName());
    }

    // Endpoint para el Administrador/Vendedor: Ver TODAS las boletas
    @GetMapping
    public List<Boleta> listarTodasLasBoletas() {
        return boletaRepository.findAll();
    }
    
    // Endpoint para cancelar boleta (requerido por tu frontend Admin)
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarBoleta(@PathVariable Long id) {
        return boletaRepository.findById(id)
                .map(boleta -> {
                    boleta.setEstado("cancelada");
                    return ResponseEntity.ok(boletaRepository.save(boleta));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Obtener una boleta por ID (Para el detalle)
    @GetMapping("/{id}")
    public ResponseEntity<Boleta> obtenerBoletaPorId(@PathVariable Long id) {
        return boletaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}