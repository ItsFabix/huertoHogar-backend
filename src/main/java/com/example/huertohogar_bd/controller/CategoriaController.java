package com.example.huertohogar_bd.controller;

import com.example.huertohogar_bd.entity.Categoria;
import com.example.huertohogar_bd.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping
    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }

    @PostMapping
    public Categoria crear(@RequestBody Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> actualizar(@PathVariable Long id, @RequestBody Categoria detalles) {
        return categoriaRepository.findById(id).map(cat -> {
            cat.setNombre(detalles.getNombre());
            cat.setImagen(detalles.getImagen());
            return ResponseEntity.ok(categoriaRepository.save(cat));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (!categoriaRepository.existsById(id)) return ResponseEntity.notFound().build();
        categoriaRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}