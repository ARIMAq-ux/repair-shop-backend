package com.example.repairshop.specification;

import com.example.repairshop.entity.RepairRequest;
import com.example.repairshop.enums.RequestStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

public class RepairRequestSpecification {

    public static Specification<RepairRequest> hasStatus(RequestStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<RepairRequest> createdAtBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            if (from != null && to != null) return cb.between(root.get("createdAt"), from, to);
            if (from != null) return cb.greaterThanOrEqualTo(root.get("createdAt"), from);
            if (to != null) return cb.lessThanOrEqualTo(root.get("createdAt"), to);
            return null;
        };
    }

    public static Specification<RepairRequest> hasActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) return null;
            if (active) {
                return cb.not(root.get("status").in(
                        List.of(RequestStatus.COMPLETED, RequestStatus.CANCELLED)));
            } else {
                return root.get("status").in(
                        List.of(RequestStatus.COMPLETED, RequestStatus.CANCELLED));
            }
        };
    }
}