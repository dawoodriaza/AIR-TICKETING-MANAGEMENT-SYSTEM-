package com.ga.airticketmanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.FetchMode;
import org.hibernate.annotations.Fetch;
import org.springframework.boot.context.properties.bind.DefaultValue;



import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@ToString(exclude = {"password","userProfile"})

public class User {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String emailAddress;

    @Column
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "is_active", nullable = false)
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private boolean active = true;

    @Column(nullable = false)
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private boolean emailVerified = false;

    @Column
    @Enumerated(EnumType.STRING)
    private Role role= Role.CUSTOMER;

    @OneToOne(
        cascade = CascadeType.ALL, fetch = FetchType.LAZY
    )
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    private UserProfile userProfile;

    @OneToMany(mappedBy = "user", fetch =  FetchType.LAZY)
    @JsonIgnore
    private List<Airport> createdAirports;

    @OneToMany(mappedBy = "user", fetch =  FetchType.LAZY)
    @JsonIgnore
    private List<Flight> createdFlights;

    @OneToMany(mappedBy = "user", fetch =  FetchType.LAZY)
    @JsonIgnore
    private List<Booking> bookings;



    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @PrePersist
    public void prePersist(){
        this.emailVerified = false;
        this.active = true;

    }
}
