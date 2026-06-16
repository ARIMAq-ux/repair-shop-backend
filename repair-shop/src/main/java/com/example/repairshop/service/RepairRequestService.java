package com.example.repairshop.service;

import com.example.repairshop.dto.RepairRequestDto;
import com.example.repairshop.dto.RepairRequestStatusUpdateDto;
import com.example.repairshop.dto.RepairRequestUpdateDto;
import com.example.repairshop.entity.*;
import com.example.repairshop.enums.RequestStatus;
import com.example.repairshop.exception.ResourceNotFoundException;
import com.example.repairshop.repository.*;
import com.example.repairshop.specification.RepairRequestSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RepairRequestService {

    private final RepairRequestRepository requestRepo;
    private final ClientRepository clientRepo;
    private final DeviceRepository deviceRepo;
    private final TechnicianRepository techRepo;

    @Transactional(readOnly = true)
    public List<RepairRequestDto> findAll(String statusStr, LocalDateTime from, LocalDateTime to, Boolean active) {
        Specification<RepairRequest> spec = Specification.where(null);
        if (statusStr != null && !statusStr.isBlank()) {
            RequestStatus status = RequestStatus.valueOf(statusStr.toUpperCase());
            spec = spec.and(RepairRequestSpecification.hasStatus(status));
        }
        if (from != null || to != null) {
            spec = spec.and(RepairRequestSpecification.createdAtBetween(from, to));
        }
        if (active != null) {
            spec = spec.and(RepairRequestSpecification.hasActive(active));
        }
        return requestRepo.findAll(spec).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RepairRequestDto findById(Long id) {
        return requestRepo.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Заявка с ID " + id + " не найдена"));
    }

    @Transactional(readOnly = true)
    public List<RepairRequestDto> findByStatus(String status) {
        return requestRepo.findByStatus(RequestStatus.valueOf(status.toUpperCase()))
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RepairRequestDto> findByClient(Long clientId) {
        clientRepo.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент с ID " + clientId + " не найден"));
        return requestRepo.findByClientId(clientId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RepairRequestDto> findByTechnician(Long techId) {
        techRepo.findById(techId)
                .orElseThrow(() -> new ResourceNotFoundException("Техник с ID " + techId + " не найден"));
        return requestRepo.findByTechnicianId(techId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public RepairRequestDto create(Long clientId, Long deviceId, String problemDescription) {
        Client c = clientRepo.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент с ID " + clientId + " не найден"));
        Device d = deviceRepo.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Устройство с ID " + deviceId + " не найдено"));
        RepairRequest r = RepairRequest.builder()
                .client(c).device(d).problemDescription(problemDescription)
                .status(RequestStatus.NEW).build();
        return toDto(requestRepo.save(r));
    }

    @Transactional
    public RepairRequestDto update(Long id, RepairRequestUpdateDto dto) {
        RepairRequest r = requestRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Заявка с ID " + id + " не найдена"));
        Client c = clientRepo.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Клиент с ID " + dto.getClientId() + " не найден"));
        Device d = deviceRepo.findById(dto.getDeviceId())
                .orElseThrow(() -> new ResourceNotFoundException("Устройство с ID " + dto.getDeviceId() + " не найдено"));
        r.setClient(c);
        r.setDevice(d);
        r.setProblemDescription(dto.getProblemDescription());
        if (dto.getTechnicianId() != null) {
            Technician t = techRepo.findById(dto.getTechnicianId())
                    .orElseThrow(() -> new ResourceNotFoundException("Техник с ID " + dto.getTechnicianId() + " не найден"));
            r.setTechnician(t);
        }
        return toDto(requestRepo.save(r));
    }

    @Transactional
    public RepairRequestDto updateStatus(Long id, RepairRequestStatusUpdateDto dto) {
        RepairRequest r = requestRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Заявка с ID " + id + " не найдена"));
        r.setStatus(RequestStatus.valueOf(dto.getStatus().toUpperCase()));
        if (dto.getTechnicianId() != null) {
            Technician t = techRepo.findById(dto.getTechnicianId())
                    .orElseThrow(() -> new ResourceNotFoundException("Техник с ID " + dto.getTechnicianId() + " не найден"));
            r.setTechnician(t);
        }
        if (dto.getEstimatedCost() != null) r.setEstimatedCost(dto.getEstimatedCost());
        if (dto.getFinalCost() != null) r.setFinalCost(dto.getFinalCost());
        if ("COMPLETED".equalsIgnoreCase(dto.getStatus())) r.setCompletedAt(LocalDateTime.now());
        return toDto(requestRepo.save(r));
    }

    @Transactional
    public void delete(Long id) {
        if (!requestRepo.existsById(id))
            throw new ResourceNotFoundException("Заявка с ID " + id + " не найдена");
        requestRepo.deleteById(id);
    }

    private RepairRequestDto toDto(RepairRequest r) {
        return RepairRequestDto.builder()
                .id(r.getId()).clientId(r.getClient().getId()).clientName(r.getClient().getFullName())
                .deviceId(r.getDevice().getId()).deviceInfo(r.getDevice().getBrand() + " " + r.getDevice().getModel())
                .deviceType(r.getDevice().getDeviceType().name())
                .problemDescription(r.getProblemDescription()).status(r.getStatus().name())
                .technicianId(r.getTechnician() != null ? r.getTechnician().getId() : null)
                .technicianName(r.getTechnician() != null ? r.getTechnician().getFullName() : null)
                .estimatedCost(r.getEstimatedCost()).finalCost(r.getFinalCost())
                .createdAt(r.getCreatedAt()).completedAt(r.getCompletedAt()).build();
    }
}