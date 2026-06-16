package com.example.repairshop.repository;

import com.example.repairshop.entity.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import java.util.Optional;

public interface TechnicianRepository extends JpaRepository<Technician, Long>,
        JpaSpecificationExecutor<Technician> {
    List<Technician> findByActiveTrue();
    Optional<Technician> findByPhone(String phone);
}