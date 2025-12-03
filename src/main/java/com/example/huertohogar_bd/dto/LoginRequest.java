package com.example.huertohogar_bd.dto;
import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}