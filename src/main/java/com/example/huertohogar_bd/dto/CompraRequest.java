package com.example.huertohogar_bd.dto;
import lombok.Data;
import java.util.List;

@Data
public class CompraRequest {
    private String direccionEnvio; // <--- AGREGAR ESTO
    private List<ItemPedido> items;

    @Data
    public static class ItemPedido {
        private String codigo;
        private Integer cantidad;
    }
}