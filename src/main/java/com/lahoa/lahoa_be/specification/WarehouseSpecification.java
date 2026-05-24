package com.lahoa.lahoa_be.specification;

import com.lahoa.lahoa_be.common.enums.ProductStatus;
import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.dto.filter.MaterialCategoryFilterRequestDTO;
import com.lahoa.lahoa_be.dto.filter.WarehouseFilterRequestDTO;
import com.lahoa.lahoa_be.entity.MaterialCategoryEntity;
import com.lahoa.lahoa_be.entity.WarehouseEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class WarehouseSpecification {

    public static Specification<WarehouseEntity> filter(WarehouseFilterRequestDTO req) {
        return (root, query, cb) -> {

            List<Predicate>
                    predicates =
                    new ArrayList<>();

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

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
