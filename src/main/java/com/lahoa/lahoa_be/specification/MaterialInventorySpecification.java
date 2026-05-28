package com.lahoa.lahoa_be.specification;

import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.dto.filter.MaterialInventoryFilterRequestDTO;
import com.lahoa.lahoa_be.entity.MaterialEntity;
import com.lahoa.lahoa_be.entity.MaterialInventoryEntity;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class MaterialInventorySpecification {

    public static Specification<MaterialEntity> filter(MaterialInventoryFilterRequestDTO req) {
        return (root, query, cb) -> {

            root.fetch(
                    "inventories",
                    JoinType.LEFT
            );

            if (query != null) {
                query.distinct(true);
            }

            List<Predicate> predicates = new ArrayList<>();

            if (req.getKeyword() != null && !req.getKeyword().isBlank()) {
                String keyword =
                        "%" + req.getKeyword().toLowerCase() + "%";

                predicates.add(
                        cb.like(cb.lower(root.get("name")), keyword)
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

            if (req.getWarehouseId() != null) {
                Join<MaterialEntity, MaterialInventoryEntity> inventoryJoin =
                        root.join(
                                "inventories",
                                JoinType.LEFT
                        );

                predicates.add(
                        cb.equal(inventoryJoin
                                        .get("warehouse")
                                        .get("id"),
                                req.getWarehouseId()
                        )
                );
            }

            Subquery<Integer> availableSubquery =
                    query.subquery(Integer.class);

            Root<MaterialInventoryEntity> inventoryRoot =
                    availableSubquery.from(
                            MaterialInventoryEntity.class
                    );

            Expression<Integer> availableExpr =
                    cb.diff(
                            cb.sum(inventoryRoot.get("onHand")),
                            cb.sum(inventoryRoot.get("reserved"))
                    );

            availableSubquery.select(availableExpr);

            availableSubquery.where(
                    cb.equal(
                            inventoryRoot.get("material").get("id"),
                            root.get("id")
                    )
            );

            /*
             * out of stock
             */
            if (Boolean.TRUE.equals(req.getOutOfStock())) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                availableSubquery,
                                0
                        )
                );
            }

            /*
             * low stock
             */
            if (Boolean.TRUE.equals(req.getLowStock())) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                availableSubquery,
                                root.get("lowStockThreshold")
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0])
            );
        };
    }
}
