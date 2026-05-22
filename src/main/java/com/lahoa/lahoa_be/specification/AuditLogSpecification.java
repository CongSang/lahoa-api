package com.lahoa.lahoa_be.specification;

import com.lahoa.lahoa_be.dto.filter.AuditLogFilterDTO;
import com.lahoa.lahoa_be.entity.AuditLogEntity;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuditLogSpecification {

    public static Specification<AuditLogEntity> filter(
            AuditLogFilterDTO req
    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (req.getEntityName() != null &&
                    !req.getEntityName().isBlank()) {

                predicates.add(
                        cb.equal(root.get("entityName"), req.getEntityName())
                );
            }

            if (req.getAction() != null) {

                predicates.add(
                        cb.equal(root.get("action"), req.getAction())
                );
            }

            if (req.getUserId() != null) {

                predicates.add(
                        cb.equal(root.get("userId"), req.getUserId())
                );
            }

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
                                        cb.lower(root.get("entityLabel")),
                                        keyword
                                ),
                                cb.like(
                                        cb.lower(root.get("userEmail")),
                                        keyword
                                )
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}