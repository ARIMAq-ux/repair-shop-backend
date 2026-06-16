package com.example.repairshop.controller;

import com.example.repairshop.dto.DeviceDto;
import com.example.repairshop.dto.DeviceUpdateDto;
import com.example.repairshop.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
@Tag(name = "Устройства", description = "Управление устройствами клиентов")
@SecurityRequirement(name = "bearerAuth")
public class DeviceController {
    private final DeviceService service;

    @GetMapping
    @Operation(summary = "Список устройств", description = "Фильтрация по дате создания и активности")
    public ResponseEntity<List<DeviceDto>> getAll(
            @Parameter(description = "Начальная дата (ISO)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @Parameter(description = "Конечная дата (ISO)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @Parameter(description = "Только активные (true) или неактивные (false)")
            @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(service.findAll(from, to, active));
    }

    @Operation(summary = "Устройство по ID", description = "Получить устройство по его идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Устройство найдено"),
            @ApiResponse(responseCode = "404", description = "Устройство не найдено")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DeviceDto> getById(
            @Parameter(description = "ID устройства", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Устройства клиента", description = "Получить все устройства конкретного клиента")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список устройств клиента"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден")
    })
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<DeviceDto>> getByClient(
            @Parameter(description = "ID клиента", example = "1") @PathVariable Long clientId) {
        return ResponseEntity.ok(service.findByClientId(clientId));
    }

    @Operation(summary = "Создать устройство", description = "Добавить новое устройство. Доступно USER и ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Устройство создано"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден")
    })
    @PostMapping
    public ResponseEntity<DeviceDto> create(@Valid @RequestBody DeviceUpdateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @Operation(summary = "Обновить устройство", description = "Изменить данные устройства. Доступно USER и ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Устройство обновлено"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
            @ApiResponse(responseCode = "404", description = "Устройство или клиент не найдены")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DeviceDto> update(
            @Parameter(description = "ID устройства", example = "1") @PathVariable Long id,
            @Valid @RequestBody DeviceUpdateDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Удалить устройство", description = "Удалить устройство и связанные с ним заявки. Только для ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Устройство удалено"),
            @ApiResponse(responseCode = "404", description = "Устройство не найдено"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID устройства", example = "1") @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}