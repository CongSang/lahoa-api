package com.lahoa.lahoa_be.specification;

import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.dto.filter.MaterialFilterRequestDTO;
import com.lahoa.lahoa_be.entity.MaterialEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class MaterialSpecification {

    public static Specification<MaterialEntity> filter(MaterialFilterRequestDTO req) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (req.getKeyword() != null && !req.getKeyword().isBlank()) {
                String keyword =
                        "%" + req.getKeyword().toLowerCase() + "%";

                predicates.add(
                        cb.or(
                                cb.like(cb.lower(root.get("name")), keyword),
                                cb.like(cb.lower(root.get("code")), keyword)
                        )
                );
            }

            if (req.getCategoryId() != null) {
                predicates.add(
                        cb.equal(root.get("category").get("id"), req.getCategoryId())
                );
            }

            if (req.getStatus() != null) {
                predicates.add(
                        cb.equal(root.get("status"), req.getStatus())
                );
            } else {
                // default: exclude DELETED
                predicates.add(
                        cb.notEqual(root.get("status"), Status.DELETED)
                );
            }

            if (req.getLowStock() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("lowStockThreshold"), req.getLowStock())
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
