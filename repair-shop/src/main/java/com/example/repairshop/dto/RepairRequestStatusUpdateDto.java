package com.example.repairshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class RepairRequestStatusUpdateDto {

    @NotBlank(message = "Статус обязателен")
    @Pattern(regexp = "NEW|DIAGNOSTICS|IN_PROGRESS|WAITING_PARTS|READY|COMPLETED|CANCELLED",
            message = "Неверный статус. Допустимые: NEW, DIAGNOSTICS, IN_PROGRESS, WAITING_PARTS, READY, COMPLETED, CANCELLED")
    private String status;

    @Positive(message = "ID техника должен быть положительным числом")
    private Long technicianId;

    @PositiveOrZero(message = "Стоимость не может быть отрицательной")
    private BigDecimal estimatedCost;

    @PositiveOrZero(message = "Стоимость не может быть отрицательной")
    private BigDecimal finalCost;
}