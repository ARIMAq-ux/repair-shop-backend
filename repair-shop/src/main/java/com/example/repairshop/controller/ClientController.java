package com.example.repairshop.controller;

import com.example.repairshop.dto.ClientDto;
import com.example.repairshop.dto.ClientUpdateDto;
import com.example.repairshop.service.ClientService;
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
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Tag(name = "Клиенты", description = "Управление клиентами сервисного центра")
@SecurityRequirement(name = "bearerAuth")
public class ClientController {
    private final ClientService service;

    @GetMapping
    @Operation(summary = "Список клиентов", description = "Фильтрация по дате создания и активности")
    public ResponseEntity<List<ClientDto>> getAll(
            @Parameter(description = "Начальная дата (ISO: 2026-06-01T00:00:00)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @Parameter(description = "Конечная дата (ISO: 2026-06-13T23:59:59)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @Parameter(description = "Только активные (true) или неактивные (false)")
            @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(service.findAll(from, to, active));
    }

    @Operation(summary = "Клиент по ID", description = "Получить клиента по его идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Клиент найден"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> getById(
            @Parameter(description = "ID клиента", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Создать клиента", description = "Добавить нового клиента. Только для ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Клиент создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClientDto> create(@Valid @RequestBody ClientUpdateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @Operation(summary = "Обновить клиента", description = "Изменить данные клиента. Только для ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Клиент обновлён"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClientDto> update(
            @Parameter(description = "ID клиента", example = "1") @PathVariable Long id,
            @Valid @RequestBody ClientUpdateDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Удалить клиента", description = "Удалить клиента по ID. Только для ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Клиент удалён"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID клиента", example = "1") @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}