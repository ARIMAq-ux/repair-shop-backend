package com.example.repairshop.service;

import com.example.repairshop.dto.TechnicianDto;
import com.example.repairshop.dto.TechnicianUpdateDto;
import com.example.repairshop.entity.RepairRequest;
import com.example.repairshop.entity.Technician;
import com.example.repairshop.enums.RequestStatus;
import com.example.repairshop.exception.ResourceNotFoundException;
import com.example.repairshop.exception.TechnicianAlreadyExistsException;
import com.example.repairshop.repository.RepairRequestRepository;
import com.example.repairshop.repository.TechnicianRepository;
import com.example.repairshop.specification.TechnicianSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TechnicianService {

    private final TechnicianRepository technicianRepository;
    private final RepairRequestRepository requestRepository;

    @Transactional(readOnly = true)
    public List<TechnicianDto> findAll(LocalDateTime from, LocalDateTime to, Boolean active) {
        Specification<Technician> spec = Specification.where(null);
        if (from != null || to != null) {
            spec = spec.and(TechnicianSpecification.createdAtBetween(from, to));
        }
        if (active != null) {
            spec = spec.and(TechnicianSpecification.hasActive(active));
        }
        return technicianRepository.findAll(spec).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TechnicianDto findById(Long id) {
        return technicianRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Техник с ID " + id + " не найден"));
    }

    @Transactional
    public TechnicianDto create(TechnicianUpdateDto dto) {
        technicianRepository.findByPhone(dto.getPhone()).ifPresent(t -> {
            throw new TechnicianAlreadyExistsException(
                    "Техник с телефоном " + dto.getPhone() + " уже существует (ID: " + t.getId() + ")");
        });

        Technician tech = Technician.builder()
                .fullName(dto.getFullName())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .specialization(dto.getSpecialization())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
        return toDto(technicianRepository.save(tech));
    }

    @Transactional
    public TechnicianDto update(Long id, TechnicianUpdateDto dto) {
        Technician tech = technicianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Техник с ID " + id + " не найден"));
        tech.setFullName(dto.getFullName());
        tech.setPhone(dto.getPhone());
        tech.setEmail(dto.getEmail());
        tech.setSpecialization(dto.getSpecialization());
        if (dto.getActive() != null) {
            tech.setActive(dto.getActive());
        }
        return toDto(technicianRepository.save(tech));
    }

    @Transactional
    public void delete(Long id) {
        Technician tech = technicianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Техник с ID " + id + " не найден"));

        // Удаляем заявки техника
        requestRepository.deleteAll(tech.getRequests());
        // Удаляем техника
        technicianRepository.delete(tech);
    }
    private TechnicianDto toDto(Technician t) {
        List<RepairRequest> requests = requestRepository.findByTechnicianId(t.getId());
        long active = requests.stream()
                .filter(r -> r.getStatus() != RequestStatus.COMPLETED && r.getStatus() != RequestStatus.CANCELLED)
                .count();
        return TechnicianDto.builder()
                .id(t.getId()).fullName(t.getFullName())
                .phone(t.getPhone()).email(t.getEmail())
                .specialization(t.getSpecialization())
                .active(t.isActive()).activeRequests(active)
                .createdAt(t.getCreatedAt())
                .build();
    }
}