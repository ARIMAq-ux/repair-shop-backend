package com.example.repairshop.repository;

import com.example.repairshop.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long>,
        JpaSpecificationExecutor<Client> {
    List<Client> findByFullNameContainingIgnoreCase(String name);
}