package com.example.repairshop.controller;

import com.example.repairshop.dto.TechnicianDto;
import com.example.repairshop.dto.TechnicianUpdateDto;
import com.example.repairshop.service.TechnicianService;
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
@RequestMapping("/api/technicians")
@RequiredArgsConstructor
@Tag(name = "Техники", description = "Управление техниками сервисного центра")
@SecurityRequirement(name = "bearerAuth")
public class TechnicianController {
    private final TechnicianService service;

    @GetMapping
    @Operation(summary = "Список техников", description = "Фильтрация по дате создания и активности")
    public ResponseEntity<List<TechnicianDto>> getAll(
            @Parameter(description = "Начальная дата (ISO)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @Parameter(description = "Конечная дата (ISO)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @Parameter(description = "Только активные (true) или неактивные (false)")
            @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(service.findAll(from, to, active));
    }

    @Operation(summary = "Техник по ID", description = "Получить техника по его идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Техник найден"),
            @ApiResponse(responseCode = "404", description = "Техник не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TechnicianDto> getById(
            @Parameter(description = "ID техника", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Создать техника", description = "Добавить нового техника. Только для ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Техник создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TechnicianDto> create(@Valid @RequestBody TechnicianUpdateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @Operation(summary = "Обновить техника", description = "Изменить данные техника. Только для ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Техник обновлён"),
            @ApiResponse(responseCode = "404", description = "Техник не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TechnicianDto> update(
            @Parameter(description = "ID техника", example = "1") @PathVariable Long id,
            @Valid @RequestBody TechnicianUpdateDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Удалить техника", description = "Удалить техника по ID. Только для ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Техник удалён"),
            @ApiResponse(responseCode = "404", description = "Техник не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID техника", example = "1") @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}