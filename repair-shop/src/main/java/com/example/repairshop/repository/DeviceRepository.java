package com.example.repairshop.repository;

import com.example.repairshop.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, Long>,
        JpaSpecificationExecutor<Device> {
    List<Device> findByClientId(Long clientId);
}