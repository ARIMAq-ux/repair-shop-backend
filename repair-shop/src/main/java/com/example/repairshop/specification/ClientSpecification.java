package com.example.repairshop.specification;

import com.example.repairshop.entity.Client;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class ClientSpecification {

    public static Specification<Client> createdAtBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            if (from != null && to != null) return cb.between(root.get("createdAt"), from, to);
            if (from != null) return cb.greaterThanOrEqualTo(root.get("createdAt"), from);
            if (to != null) return cb.lessThanOrEqualTo(root.get("createdAt"), to);
            return null;
        };
    }

    public static Specification<Client> hasActive(Boolean active) {
        return (root, query, cb) -> active == null ? null : cb.equal(root.get("active"), active);
    }
}