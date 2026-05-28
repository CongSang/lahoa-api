package com.lahoa.lahoa_be.specification;

import com.lahoa.lahoa_be.dto.filter.MaterialInventoryFilterRequestDTO;
import com.lahoa.lahoa_be.dto.filter.MaterialInventoryMovementFilterRequestDTO;
import com.lahoa.lahoa_be.entity.MaterialInventoryEntity;
import com.lahoa.lahoa_be.entity.MaterialInventoryMovementEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class MaterialInventoryMovementSpecification {

    public static Specification<MaterialInventoryMovementEntity> filter(MaterialInventoryMovementFilterRequestDTO req) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (req.getKeyword() != null && !req.getKeyword().isBlank()) {
                String keyword =
                        "%" + req.getKeyword().toLowerCase() + "%";

                predicates.add(
                        cb.or(
                                cb.like(cb.lower(root.get("code")), keyword),
                                cb.like(cb.lower(root.get("material").get("name")), keyword),
                                cb.like(cb.lower(root.get("warehouse").get("name")), keyword),
                                cb.like(cb.lower(root.get("actorEmail")), keyword)
                        )
                );
            }

            if (req.getMaterialId() != null) {
                predicates.add(
                        cb.equal(
                                root.get("material").get("id"),
                                req.getMaterialId()
                        )
                );
            }

            if (req.getWarehouseId() != null) {
                predicates.add(
                        cb.equal(
                                root.get("warehouse").get("id"),
                                req.getWarehouseId()
                        )
                );
            }

            if (req.getType() != null) {
                predicates.add(
                        cb.equal(
                                root.get("type"),
                                req.getType()
                        )
                );
            }

            if (req.getReferenceType() != null) {
                predicates.add(
                        cb.equal(
                                root.get("referenceType"),
                                req.getReferenceType()
                        )
                );
            }

            if (req.getReferenceId() != null && !req.getReferenceId().isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("referenceId")),
                                "%" + req.getReferenceId().toLowerCase() + "%"
                        )
                );
            }

            if (query != null) {
                query.orderBy(
                        cb.desc(
                                root.get("createdAt")
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0])
            );
        };
    }
}
