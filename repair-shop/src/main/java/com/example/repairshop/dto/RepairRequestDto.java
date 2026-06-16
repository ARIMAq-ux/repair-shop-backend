package com.example.repairshop.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RepairRequestDto {
    private Long id;

    @NotNull(message = "ID клиента обязателен")
    @Positive(message = "ID клиента должен быть положительным числом")
    private Long clientId;

    private String clientName;

    @NotNull(message = "ID устройства обязателен")
    @Positive(message = "ID устройства должен быть положительным числом")
    private Long deviceId;

    private String deviceInfo;
    private String deviceType;

    @NotBlank(message = "Описание проблемы обязательно")
    @Size(min = 5, max = 1000, message = "Описание должно быть от 5 до 1000 символов")
    private String problemDescription;

    private String status;

    @Positive(message = "ID техника должен быть положительным числом")
    private Long technicianId;

    private String technicianName;

    @PositiveOrZero(message = "Стоимость не может быть отрицательной")
    @Digits(integer = 10, fraction = 2, message = "Неверный формат стоимости")
    private BigDecimal estimatedCost;

    @PositiveOrZero(message = "Стоимость не может быть отрицательной")
    @Digits(integer = 10, fraction = 2, message = "Неверный формат стоимости")
    private BigDecimal finalCost;

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}