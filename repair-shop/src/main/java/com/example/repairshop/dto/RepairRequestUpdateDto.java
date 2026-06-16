package com.example.repairshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RepairRequestUpdateDto {

    @NotNull(message = "ID клиента обязателен")
    @Positive(message = "ID клиента должен быть положительным числом")
    private Long clientId;

    @NotNull(message = "ID устройства обязателен")
    @Positive(message = "ID устройства должен быть положительным числом")
    private Long deviceId;

    @NotBlank(message = "Описание проблемы обязательно")
    @Size(min = 5, max = 1000, message = "Описание должно быть от 5 до 1000 символов")
    private String problemDescription;

    @Positive(message = "ID техника должен быть положительным числом")
    private Long technicianId;
}