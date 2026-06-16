package com.example.repairshop.service;

import com.example.repairshop.dto.DeviceDto;
import com.example.repairshop.dto.DeviceUpdateDto;
import com.example.repairshop.entity.Client;
import com.example.repairshop.entity.Device;
import com.example.repairshop.entity.RepairRequest;
import com.example.repairshop.exception.ResourceNotFoundException;
import com.example.repairshop.repository.ClientRepository;
import com.example.repairshop.repository.DeviceRepository;
import com.example.repairshop.repository.RepairRequestRepository;
import com.example.repairshop.specification.DeviceSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final ClientRepository clientRepository;
    private final RepairRequestRepository requestRepo;

    @Transactional(readOnly = true)
    public List<DeviceDto> findAll(LocalDateTime from, LocalDateTime to, Boolean active) {
        Specification<Device> spec = Specification.where(null);
        if (from != null || to != null) {
            spec = spec.and(DeviceSpecification.createdAtBetween(from, to));
        }
        if (active != null) {
            spec = spec.and(DeviceSpecification.hasActive(active));
        }
        return deviceRepository.findAll(spec).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DeviceDto findById(Long id) {
        return deviceRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Устройство с ID " + id + " не найдено"));
    }

    @Transactional(readOnly = true)
    public List<DeviceDto> findByClientId(Long clientId) {
        clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент с ID " + clientId + " не найден"));
        return deviceRepository.findByClientId(clientId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public DeviceDto create(DeviceUpdateDto dto) {
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Клиент с ID " + dto.getClientId() + " не найден"));
        Device device = Device.builder()
                .client(client)
                .deviceType(dto.getDeviceType())
                .brand(dto.getBrand())
                .model(dto.getModel())
                .serialNumber(dto.getSerialNumber())
                .description(dto.getDescription())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
        return toDto(deviceRepository.save(device));
    }

    @Transactional
    public DeviceDto update(Long id, DeviceUpdateDto dto) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Устройство с ID " + id + " не найдено"));
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Клиент с ID " + dto.getClientId() + " не найден"));
        device.setClient(client);
        device.setDeviceType(dto.getDeviceType());
        device.setBrand(dto.getBrand());
        device.setModel(dto.getModel());
        device.setSerialNumber(dto.getSerialNumber());
        device.setDescription(dto.getDescription());
        if (dto.getActive() != null) {
            device.setActive(dto.getActive());
        }
        return toDto(deviceRepository.save(device));
    }

    @Transactional
    public void delete(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Устройство с ID " + id + " не найдено"));

        // Удаляем заявки, связанные с устройством
        requestRepo.deleteAll(requestRepo.findByDeviceId(id));
        // Удаляем устройство
        deviceRepository.delete(device);
    }

    private DeviceDto toDto(Device d) {
        return DeviceDto.builder()
                .id(d.getId()).clientId(d.getClient().getId())
                .clientName(d.getClient().getFullName()).deviceType(d.getDeviceType())
                .brand(d.getBrand()).model(d.getModel())
                .serialNumber(d.getSerialNumber()).description(d.getDescription())
                .active(d.isActive()).createdAt(d.getCreatedAt())
                .build();
    }
}