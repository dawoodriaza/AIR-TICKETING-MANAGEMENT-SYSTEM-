package com.ga.airticketmanagement.repository;



import com.ga.airticketmanagement.model.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {

    Optional<ImageEntity> findByFileName(String fileName);


    boolean existsByFileName(String fileName);

    List<ImageEntity> findByUserId(Long userId);




}