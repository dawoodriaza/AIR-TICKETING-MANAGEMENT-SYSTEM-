package com.ga.airticketmanagement.specification;

import com.ga.airticketmanagement.model.Flight;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FlightSpecification {

    public static Specification<Flight> withSearchCriteria(
            Long id,
            String flightNo,
            Long originAirportId,
            String originAirportName,
            Long destinationAirportId,
            String destinationAirportName,
            BigDecimal price
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (id != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), id));
            }

            if (flightNo != null && !flightNo.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("flightNo")),
                    "%" + flightNo.toLowerCase() + "%"
                ));
            }

            if (originAirportId != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("originAirport").get("id"),
                    originAirportId
                ));
            }

            if (originAirportName != null && !originAirportName.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("originAirport").get("name")),
                    "%" + originAirportName.toLowerCase() + "%"
                ));
            }

            if (destinationAirportId != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("destinationAirport").get("id"),
                    destinationAirportId
                ));
            }

            if (destinationAirportName != null && !destinationAirportName.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("destinationAirport").get("name")),
                    "%" + destinationAirportName.toLowerCase() + "%"
                ));
            }

            if (price != null) {
                predicates.add(criteriaBuilder.equal(root.get("price"), price));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Flight> withGeneralSearch(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String searchLower = search.toLowerCase().trim();
            List<Predicate> searchPredicates = new ArrayList<>();

            searchPredicates.add(criteriaBuilder.like(
                criteriaBuilder.lower(root.get("flightNo")),
                "%" + searchLower + "%"
            ));

            searchPredicates.add(criteriaBuilder.like(
                criteriaBuilder.lower(root.get("originAirport").get("name")),
                "%" + searchLower + "%"
            ));

            searchPredicates.add(criteriaBuilder.like(
                criteriaBuilder.lower(root.get("destinationAirport").get("name")),
                "%" + searchLower + "%"
            ));

            try {
                Long idValue = Long.parseLong(search);
                searchPredicates.add(criteriaBuilder.equal(root.get("id"), idValue));
            } catch (NumberFormatException e) {
                // Ignore
            }

            try {
                Long originAirportId = Long.parseLong(search);
                searchPredicates.add(criteriaBuilder.equal(
                    root.get("originAirport").get("id"),
                    originAirportId
                ));
            } catch (NumberFormatException e) {
                // Ignore
            }

            try {
                Long destinationAirportId = Long.parseLong(search);
                searchPredicates.add(criteriaBuilder.equal(
                    root.get("destinationAirport").get("id"),
                    destinationAirportId
                ));
            } catch (NumberFormatException e) {
                // Ignore
            }

            try {
                BigDecimal priceValue = new BigDecimal(search);
                searchPredicates.add(criteriaBuilder.equal(root.get("price"), priceValue));
            } catch (NumberFormatException e) {
                // Ignore
            }

            return criteriaBuilder.or(searchPredicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Flight> withBrowseFilters(
            Long originAirportId,
            Long destinationAirportId,
            LocalDateTime departureTimeFrom,
            LocalDateTime departureTimeTo,
            LocalDateTime arrivalTimeFrom,
            LocalDateTime arrivalTimeTo,
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (originAirportId != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("originAirport").get("id"),
                    originAirportId
                ));
            }

            if (destinationAirportId != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("destinationAirport").get("id"),
                    destinationAirportId
                ));
            }

            if (departureTimeFrom != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("departureTime"),
                    departureTimeFrom
                ));
            }

            if (departureTimeTo != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("departureTime"),
                    departureTimeTo
                ));
            }

            if (arrivalTimeFrom != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("arrivalTime"),
                    arrivalTimeFrom
                ));
            }

            if (arrivalTimeTo != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("arrivalTime"),
                    arrivalTimeTo
                ));
            }

            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("price"),
                    minPrice
                ));
            }

            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("price"),
                    maxPrice
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Flight> withFutureFlightsFilter() {
        return (root, query, criteriaBuilder) -> {
            LocalDateTime currentTime = LocalDateTime.now();
            return criteriaBuilder.greaterThan(root.get("departureTime"), currentTime);
        };
    }
}
