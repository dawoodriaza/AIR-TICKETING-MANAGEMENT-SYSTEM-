package com.ga.airticketmanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "airports")

public class Airport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String country;

    @Column
    private String code;

    @CreationTimestamp
    @Column
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column
    private LocalDateTime updatedAt;



    @OneToMany(fetch = FetchType.EAGER, mappedBy = "originAirport", orphanRemoval = true)
    @JsonIgnore
    private List<Flight> originFlights;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "destinationAirport", orphanRemoval = true)
    @JsonIgnore
    private List<Flight> destinationFlights;

    @ManyToOne
    @JoinColumn(name="user_id")
    @JsonIgnore
    private User user;

}
