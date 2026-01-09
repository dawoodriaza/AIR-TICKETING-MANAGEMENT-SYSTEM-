package com.ga.airticketmanagement.repository;

import com.ga.airticketmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmailAddress(String userEmailAddress);
    Optional<User> findUserByEmailAddress(String userEmailAddress);

}
