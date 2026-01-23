package com.ga.airticketmanagement.specification;

import com.ga.airticketmanagement.model.Booking;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class BookingSpecification {

    public static Specification<Booking> withFlightJoin() {
        return (root, query, criteriaBuilder) -> {
            if (root.getJoins().stream().noneMatch(join -> "flight".equals(join.getAttribute().getName()))) {
                root.join("flight", JoinType.LEFT);
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Booking> withSearchCriteria(
            Long id,
            Long flightId,
            Long userId,
            String status
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (id != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), id));
            }

            if (flightId != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("flight").get("id"),
                    flightId
                ));
            }

            if (userId != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("user").get("id"),
                    userId
                ));
            }

            if(status != null) {
                String lowerStatus = status.toLowerCase().trim();
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("status")),
                        lowerStatus
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Booking> withGeneralSearch(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String searchLower = search.toLowerCase().trim();
            List<Predicate> predicates = new ArrayList<>();

            try {
                Long value = Long.parseLong(search);

                predicates.add(criteriaBuilder.equal(root.get("id"), value));
                predicates.add(criteriaBuilder.equal(root.get("flight").get("id"), value));
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), value));

            } catch (NumberFormatException ignored) {
                // ignore
            }

            predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("user").get("emailAddress")),
                    "%" + searchLower + "%"
            ));

            predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("flight").get("originAirport").get("code")),
                    "%" + searchLower + "%"
            ));

            predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("flight").get("destinationAirport").get("code")),
                    "%" + searchLower + "%"
            ));

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

}
