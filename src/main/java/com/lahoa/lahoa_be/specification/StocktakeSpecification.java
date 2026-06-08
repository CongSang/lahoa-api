package com.lahoa.lahoa_be.specification;

import com.lahoa.lahoa_be.dto.filter.StocktakeFilterRequestDTO;
import com.lahoa.lahoa_be.entity.StocktakeEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StocktakeSpecification {

    public static Specification<StocktakeEntity> filter(
            StocktakeFilterRequestDTO req
    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if(req.getFromDate() != null) {
                LocalDateTime startOfDay =
                        req.getFromDate()
                                .withHour(0)
                                .withMinute(0)
                                .withSecond(0);
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("createdAt"),
                                startOfDay
                        )
                );
            }

            if(req.getToDate() != null) {
                LocalDateTime endOfDay =
                        req.getToDate()
                                .withHour(23)
                                .withMinute(59)
                                .withSecond(59);
                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("createdAt"),
                                endOfDay
                        )
                );
            }

            if (req.getKeyword() != null &&
                    !req.getKeyword().isBlank()) {

                String keyword = "%" + req.getKeyword().toLowerCase() + "%";

                predicates.add(
                        cb.or(
                                cb.like(
                                        cb.lower(root.get("code")),
                                        keyword
                                ),
                                cb.like(
                                        cb.lower(root.get("warehouse").get("name")),
                                        keyword
                                )
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

            if (query != null) {
                query.orderBy(
                        cb.desc(
                                root.get("createdAt")
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
