package com.example.huertohogar_bd.dto;

public class LoginRequest {
    private String email;
    private String password;

    // Constructor vacío
    public LoginRequest() {
    }

    // Constructor con argumentos
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters y Setters manuales para asegurar que funcionen
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}