package com.example.repairshop.service;

import com.example.repairshop.dto.ClientDto;
import com.example.repairshop.dto.ClientUpdateDto;
import com.example.repairshop.entity.Client;
import com.example.repairshop.exception.ClientAlreadyExistsException;
import com.example.repairshop.exception.ResourceNotFoundException;
import com.example.repairshop.repository.ClientRepository;
import com.example.repairshop.repository.DeviceRepository;
import com.example.repairshop.repository.RepairRequestRepository;
import com.example.repairshop.specification.ClientSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final DeviceRepository deviceRepository;
    private final RepairRequestRepository requestRepo;

    @Transactional(readOnly = true)
    public List<ClientDto> findAll(LocalDateTime from, LocalDateTime to, Boolean active) {
        Specification<Client> spec = Specification.where(null);
        if (from != null || to != null) {
            spec = spec.and(ClientSpecification.createdAtBetween(from, to));
        }
        if (active != null) {
            spec = spec.and(ClientSpecification.hasActive(active));
        }
        return clientRepository.findAll(spec).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClientDto findById(Long id) {
        return clientRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент с ID " + id + " не найден"));
    }

    @Transactional
    public ClientDto create(ClientUpdateDto dto) {
        clientRepository.findByFullNameContainingIgnoreCase(dto.getFullName()).stream()
                .filter(c -> c.getPhone().equals(dto.getPhone()))
                .findFirst()
                .ifPresent(c -> {
                    throw new ClientAlreadyExistsException(
                            "Клиент с телефоном " + dto.getPhone() + " уже существует (ID: " + c.getId() + ")");
                });

        Client client = new Client();
        client.setFullName(dto.getFullName());
        client.setPhone(dto.getPhone());
        client.setEmail(dto.getEmail());
        client.setAddress(dto.getAddress());
        if (dto.getActive() != null) {
            client.setActive(dto.getActive());
        } // иначе останется true (по умолчанию)
        return toDto(clientRepository.save(client));
    }

    @Transactional
    public ClientDto update(Long id, ClientUpdateDto dto) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент с ID " + id + " не найден"));
        client.setFullName(dto.getFullName());
        client.setPhone(dto.getPhone());
        client.setEmail(dto.getEmail());
        client.setAddress(dto.getAddress());
        if (dto.getActive() != null) {
            client.setActive(dto.getActive());
        }
        return toDto(clientRepository.save(client));
    }

    @Transactional
    public void delete(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент с ID " + id + " не найден"));

        // Удаляем заявки клиента
        requestRepo.deleteAll(client.getRequests());
        // Удаляем устройства клиента
        deviceRepository.deleteAll(client.getDevices());
        // Удаляем самого клиента
        clientRepository.delete(client);
    }

    private ClientDto toDto(Client c) {
        return ClientDto.builder()
                .id(c.getId()).fullName(c.getFullName())
                .phone(c.getPhone()).email(c.getEmail())
                .address(c.getAddress()).active(c.isActive())
                .createdAt(c.getCreatedAt())
                .build();
    }
}