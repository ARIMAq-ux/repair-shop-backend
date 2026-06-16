package com.example.repairshop.dto;

import com.example.repairshop.enums.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DeviceUpdateDto {

    @NotNull(message = "ID клиента обязателен")
    private Long clientId;

    @NotNull(message = "Тип устройства обязателен")
    private DeviceType deviceType;

    @NotBlank(message = "Бренд обязателен")
    @Size(min = 2, max = 100, message = "Бренд должен быть от 2 до 100 символов")
    private String brand;

    @NotBlank(message = "Модель обязательна")
    @Size(min = 1, max = 100, message = "Модель должна быть от 1 до 100 символов")
    private String model;

    @Size(max = 100, message = "Серийный номер не должен превышать 100 символов")
    private String serialNumber;

    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    private String description;

    private Boolean active;
}