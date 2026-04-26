package com.lahoa.lahoa_be.specification;

import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.dto.filter.ProductFilterRequestDTO;
import com.lahoa.lahoa_be.entity.ProductEntity;
import com.lahoa.lahoa_be.entity.VariantPropertyValueEntity;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

//{
//        "keyword": "hoa hồng",
//        "categoryId": 1,
//        "minPrice": 100000,
//        "maxPrice": 500000,
//        "status": "ACTIVE",
//        "propertyValueIds": {
//          "1": [10, 11],
//          "2": [20]
//        }
// }

public class ProductSpecification {

    public static Specification<ProductEntity> filter(ProductFilterRequestDTO req) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // tránh duplicate khi join
            query.distinct(true);

            // =========================
            // 1. KEYWORD
            // =========================
            if (req.getKeyword() != null && !req.getKeyword().isBlank()) {
                String keyword = "%" + req.getKeyword().toLowerCase() + "%";

                predicates.add(
                        cb.like(cb.lower(root.get("name")), keyword)
                );
            }

            // =========================
            // 2. STATUS
            // =========================
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

            // =========================
            // 3. PRICE RANGE
            // =========================
            if (req.getMinPrice() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("price"), req.getMinPrice())
                );
            }

            if (req.getMaxPrice() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("price"), req.getMaxPrice())
                );
            }

            // =========================
            // 4. CATEGORY FILTER
            // =========================
            if (req.getCategoryId() != null) {

                Join<Object, Object> mapping = root.join("categoryMappings", JoinType.INNER);

                predicates.add(
                        cb.equal(mapping.get("category").get("id"), req.getCategoryId())
                );
            }

            // =========================
            // 5. PROPERTY FILTER
            // =========================
            if (req.getPropertyValueIds() != null && !req.getPropertyValueIds().isEmpty()) {

                /**
                 * - mỗi property = 1 subquery
                 * - giữa các property = AND
                 *
                 * Ví dụ:
                 * color IN (red, blue)
                 * AND size IN (M)
                 */

                for (Map.Entry<Long, List<Long>> entry : req.getPropertyValueIds().entrySet()) {

                    Long propertyId = entry.getKey();
                    List<Long> valueIds = entry.getValue();

                    if (valueIds == null || valueIds.isEmpty()) continue;

                    // subquery
                    Subquery<Long> sub = query.subquery(Long.class);
                    Root<VariantPropertyValueEntity> subRoot = sub.from(VariantPropertyValueEntity.class);

                    sub.select(
                            subRoot.get("variant").get("product").get("id")
                    );

                    Predicate propertyPredicate = cb.and(
                            cb.equal(subRoot.get("property").get("id"), propertyId),
                            subRoot.get("value").get("id").in(valueIds)
                    );

                    sub.where(propertyPredicate);

                    // add AND condition
                    predicates.add(
                            root.get("id").in(sub)
                    );
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}