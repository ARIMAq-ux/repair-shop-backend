package com.example.repairshop.controller;

import com.example.repairshop.dto.*;
import com.example.repairshop.service.RepairRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
@Tag(name = "Заявки на ремонт", description = "Управление заявками на ремонт техники")
@SecurityRequirement(name = "bearerAuth")
@Validated
public class RequestController {
    private final RepairRequestService service;

    @GetMapping
    @Operation(summary = "Все заявки", description = "Фильтрация по статусу, дате создания и активности")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список заявок"),
            @ApiResponse(responseCode = "400", description = "Неверный статус или дата")
    })
    public ResponseEntity<List<RepairRequestDto>> getAll(
            @Parameter(description = "Статус")
            @RequestParam(required = false)
            @Pattern(regexp = "NEW|DIAGNOSTICS|IN_PROGRESS|WAITING_PARTS|READY|COMPLETED|CANCELLED") String status,
            @Parameter(description = "Начальная дата (ISO)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @Parameter(description = "Конечная дата (ISO)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @Parameter(description = "Только активные (true) или неактивные (false)")
            @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(service.findAll(status, from, to, active));
    }

    @Operation(summary = "Заявка по ID", description = "Получить заявку по идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заявка найдена"),
            @ApiResponse(responseCode = "404", description = "Заявка не найдена")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RepairRequestDto> getById(
            @Parameter(description = "ID заявки", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Заявки по статусу", description = "Получить заявки с определённым статусом")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список заявок"),
            @ApiResponse(responseCode = "400", description = "Неверный статус")
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<RepairRequestDto>> getByStatus(
            @Parameter(description = "Статус", example = "NEW")
            @PathVariable @Pattern(regexp = "NEW|DIAGNOSTICS|IN_PROGRESS|WAITING_PARTS|READY|COMPLETED|CANCELLED") String status) {
        return ResponseEntity.ok(service.findByStatus(status));
    }

    @Operation(summary = "Заявки клиента", description = "Получить все заявки конкретного клиента")
    @ApiResponse(responseCode = "200", description = "Список заявок клиента")
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<RepairRequestDto>> getByClient(
            @Parameter(description = "ID клиента", example = "1") @PathVariable Long clientId) {
        return ResponseEntity.ok(service.findByClient(clientId));
    }

    @Operation(summary = "Заявки техника", description = "Получить все заявки, назначенные на техника")
    @ApiResponse(responseCode = "200", description = "Список заявок техника")
    @GetMapping("/technician/{techId}")
    public ResponseEntity<List<RepairRequestDto>> getByTechnician(
            @Parameter(description = "ID техника", example = "1") @PathVariable Long techId) {
        return ResponseEntity.ok(service.findByTechnician(techId));
    }

    @Operation(summary = "Создать заявку", description = "Создать новую заявку на ремонт")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Заявка создана"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
            @ApiResponse(responseCode = "404", description = "Клиент или устройство не найдены")
    })
    @PostMapping
    public ResponseEntity<RepairRequestDto> create(@Valid @RequestBody RepairRequestCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(dto.getClientId(), dto.getDeviceId(), dto.getProblemDescription()));
    }

    @Operation(summary = "Обновить заявку", description = "Изменить клиента, устройство и описание заявки")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заявка обновлена"),
            @ApiResponse(responseCode = "404", description = "Заявка, клиент или устройство не найдены")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RepairRequestDto> update(
            @Parameter(description = "ID заявки", example = "1") @PathVariable Long id,
            @Valid @RequestBody RepairRequestUpdateDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Обновить статус заявки", description = "Изменить статус, назначить техника, указать стоимость")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статус обновлён"),
            @ApiResponse(responseCode = "404", description = "Заявка или техник не найдены")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<RepairRequestDto> updateStatus(
            @Parameter(description = "ID заявки", example = "1") @PathVariable Long id,
            @Valid @RequestBody RepairRequestStatusUpdateDto dto) {
        return ResponseEntity.ok(service.updateStatus(id, dto));
    }

    @Operation(summary = "Удалить заявку", description = "Удалить заявку по ID. Только для ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Заявка удалена"),
            @ApiResponse(responseCode = "404", description = "Заявка не найдена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID заявки", example = "1") @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}