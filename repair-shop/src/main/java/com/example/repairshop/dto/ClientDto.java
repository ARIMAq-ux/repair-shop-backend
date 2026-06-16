package com.example.repairshop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ClientDto {
    private Long id;

    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов")
    private String fullName;

    @NotBlank(message = "Телефон обязателен")
    @Pattern(regexp = "\\+7\\d{10}", message = "Телефон должен быть в формате +7XXXXXXXXXX (11 цифр)")
    private String phone;

    @Email(message = "Некорректный email")
    private String email;

    @Size(max = 255, message = "Адрес не должен превышать 255 символов")
    private String address;

    private boolean active;
    private LocalDateTime createdAt;
}