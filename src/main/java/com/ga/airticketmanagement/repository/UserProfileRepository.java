package com.ga.airticketmanagement.repository;

import com.ga.airticketmanagement.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

}