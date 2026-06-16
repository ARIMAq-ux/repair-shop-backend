package com.example.repairshop.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data @AllArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private String username;
}