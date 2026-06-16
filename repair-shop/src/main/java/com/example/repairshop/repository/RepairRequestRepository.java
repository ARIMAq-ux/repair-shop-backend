package com.example.repairshop.repository;

import com.example.repairshop.entity.RepairRequest;
import com.example.repairshop.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface RepairRequestRepository extends JpaRepository<RepairRequest, Long>,
        JpaSpecificationExecutor<RepairRequest> {
    List<RepairRequest> findByStatus(RequestStatus status);
    List<RepairRequest> findByClientId(Long clientId);
    List<RepairRequest> findByTechnicianId(Long technicianId);
    List<RepairRequest> findByDeviceId(Long deviceId);
    long countByStatus(RequestStatus status);
}