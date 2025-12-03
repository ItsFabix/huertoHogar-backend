package com.example.huertohogar_bd.dto;
import java.util.List;

import lombok.Data;

@Data
public class CompraRequest {
    private String direccionEnvio; 
    private List<ItemPedido> items;

    @Data
    public static class ItemPedido {
        private String codigo;
        private Integer cantidad;
    }
}