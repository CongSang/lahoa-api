package com.lahoa.lahoa_be.specification;

import com.lahoa.lahoa_be.common.enums.ProductStatus;
import com.lahoa.lahoa_be.dto.filter.ProductFilterRequestDTO;
import com.lahoa.lahoa_be.entity.ProductEntity;
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
//        },
//        "variantPropertyValueIds": {
//          "1": [10, 11],
//        }
// }

public class ProductSpecification {

    public static Specification<ProductEntity> filter(ProductFilterRequestDTO req) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // tránh duplicate khi join
            query.distinct(true);

            // 1. KEYWORD
            if (req.getKeyword() != null && !req.getKeyword().isBlank()) {
                String keyword = "%" + req.getKeyword().toLowerCase() + "%";

                predicates.add(
                        cb.like(cb.lower(root.get("name")), keyword)
                );
            }

            // 2. STATUS
            if (req.getStatus() != null) {
                predicates.add(
                        cb.equal(root.get("status"), req.getStatus())
                );
            } else {
                // default: exclude DELETED
                predicates.add(
                        cb.notEqual(root.get("status"), ProductStatus.DELETED)
                );
            }

            // 3. PRICE RANGE
            if (req.getMinPrice() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("basePrice"), req.getMinPrice())
                );
            }

            if (req.getMaxPrice() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("basePrice"), req.getMaxPrice())
                );
            }

            // 4. CATEGORY FILTER
            if (req.getCategoryId() != null) {

                Join<Object, Object> mapping = root.join("categoryMappings", JoinType.INNER);

                predicates.add(
                        cb.equal(mapping.get("category").get("id"), req.getCategoryId())
                );
            }

            // 5. PROPERTY FILTER
            if (req.getPropertyValueIds() != null && !req.getPropertyValueIds().isEmpty()) {
                // product → productPropertyValues
                Join<Object, Object> ppvJoin = root.join("propertyValues", JoinType.INNER);

                // ppv → propertyValue
                Join<Object, Object> valueJoin = ppvJoin.join("propertyValue", JoinType.INNER);

                // propertyValue → property
                Join<Object, Object> propertyJoin = valueJoin.join("property", JoinType.INNER);

                List<Predicate> orPredicates = new ArrayList<>();

                for (Map.Entry<Long, List<Long>> entry : req.getPropertyValueIds().entrySet()) {

                    Long propertyId = entry.getKey();
                    List<Long> valueIds = entry.getValue();

                    if (valueIds == null || valueIds.isEmpty()) continue;

                    // (property = X AND value IN (...))
                    orPredicates.add(
                            cb.and(
                                    cb.equal(propertyJoin.get("id"), propertyId),
                                    valueJoin.get("id").in(valueIds)
                            )
                    );
                }

                // OR tất cả property condition
                predicates.add(cb.or(orPredicates.toArray(new Predicate[0])));

                // GROUP BY product.id
                query.groupBy(root.get("id"));

                // HAVING đủ số property
                query.having(
                        cb.equal(
                                cb.countDistinct(propertyJoin.get("id")),
                                req.getPropertyValueIds().size()
                        )
                );
            }

//            if (req.getVariantPropertyValueIds() != null && !req.getVariantPropertyValueIds().isEmpty()) {
//
//                Subquery<Long> sub = query.subquery(Long.class);
//
//                Root<VariantPropertyValueEntity> subRoot = sub.from(VariantPropertyValueEntity.class);
//
//                Join<?, ?> variantJoin = subRoot.join("variant");
//                Join<?, ?> valueJoin = subRoot.join("propertyValue");
//
//                sub.select(variantJoin.get("product").get("id"));
//
//                sub.where(
//                        cb.and(
//                                cb.equal(variantJoin.get("product").get("id"), root.get("id")),
//                                valueJoin.get("id").in(req.getVariantPropertyValueIds())
//                        )
//                );
//
//                predicates.add(cb.exists(sub));
//            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}