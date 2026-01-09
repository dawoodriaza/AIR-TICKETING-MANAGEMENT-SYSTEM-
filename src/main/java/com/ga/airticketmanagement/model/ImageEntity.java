package com.ga.airticketmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity

@Table(name = "Assets")
// @Data - Lombok annotation that generates getters, setters, toString, equals, hashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String fileName;

    @Column(nullable = true)
    private Long userId;

    @Column(nullable = false)
    private String originalFileName;


    @Column(nullable = false)
    private String fileType;


    private Long fileSize;


    @Column(nullable = false)
    private String filePath;


    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
    }
}