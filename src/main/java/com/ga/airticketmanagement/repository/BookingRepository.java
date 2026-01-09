package com.ga.airticketmanagement.repository;

import com.ga.airticketmanagement.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {

    Booking findByName(String categoryName);
}




