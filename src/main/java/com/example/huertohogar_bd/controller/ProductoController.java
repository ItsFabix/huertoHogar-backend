package com.example.huertohogar_bd.controller;

import com.example.huertohogar_bd.entity.Producto;
import com.example.huertohogar_bd.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    // LEER (Read)
    @GetMapping
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Producto> obtenerProducto(@PathVariable String codigo) {
        return productoRepository.findById(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CREAR (Create) - Solo Admins (protegido por SecurityConfig)
    @PostMapping
    public Producto crearProducto(@RequestBody Producto producto) {
        return productoRepository.save(producto);
    }

    // ACTUALIZAR (Update)
    @PutMapping("/{codigo}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable String codigo, @RequestBody Producto detalles) {
        return productoRepository.findById(codigo)
                .map(prod -> {
                    prod.setNombre(detalles.getNombre());
                    prod.setPrecio(detalles.getPrecio());
                    prod.setStock(detalles.getStock());
                    prod.setCategoria(detalles.getCategoria());
                    prod.setImagen(detalles.getImagen());
                    prod.setDescripcion(detalles.getDescripcion());
                    prod.setOferta(detalles.getOferta());
                    prod.setPrecioOferta(detalles.getPrecioOferta());
                    return ResponseEntity.ok(productoRepository.save(prod));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ELIMINAR (Delete)
    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> eliminarProducto(@PathVariable String codigo) {
        if (!productoRepository.existsById(codigo)) {
            return ResponseEntity.notFound().build();
        }
        productoRepository.deleteById(codigo);
        return ResponseEntity.ok().build();
    }
}