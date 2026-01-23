package com.ga.airticketmanagement.specification;

import com.ga.airticketmanagement.model.User;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
@Slf4j
public class UserSpecification {

    public static Specification<User> withSearchCriteria(Long id, String email, Boolean active, Boolean emaileVerified) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (id != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), id));
            }

            if (email != null && !email.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("emailAddress")),
                    "%" + email.toLowerCase() + "%"
                ));
            }

            if(active != null){
                predicates.add(criteriaBuilder.equal(root.get("active"), active));
            }

            if(emaileVerified != null){
                predicates.add(criteriaBuilder.equal(root.get("emailVerified"), emaileVerified));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<User> withGeneralSearch(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String searchLower = search.toLowerCase().trim();
            List<Predicate> searchPredicates = new ArrayList<>();

            searchPredicates.add(criteriaBuilder.like(
                criteriaBuilder.lower(root.get("emailAddress")),
                "%" + searchLower + "%"
            ));


            Expression<String> firstName = criteriaBuilder.coalesce(root.get("userProfile").get("firstName"), "");
            Expression<String> lastName = criteriaBuilder.coalesce(root.get("userProfile").get("lastName"), "");

            Expression<String> fullName = criteriaBuilder.concat(
                    criteriaBuilder.concat(criteriaBuilder.lower(firstName), " "),
                    criteriaBuilder.lower(lastName)
            );

            searchPredicates.add(criteriaBuilder.like(fullName, "%" + search.toLowerCase() + "%"));


            try {
                Long idValue = Long.parseLong(search);
                searchPredicates.add(criteriaBuilder.equal(root.get("id"), idValue));
            } catch (NumberFormatException e) {
                // Ignore
            }

            return criteriaBuilder.or(searchPredicates.toArray(new Predicate[0]));
        };
    }
}
