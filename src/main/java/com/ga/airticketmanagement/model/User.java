package com.ga.airticketmanagement.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.context.properties.bind.DefaultValue;

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

    @Column
    private Boolean isEmailVerified;

    @Column
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(
        cascade = CascadeType.ALL, fetch =FetchType.LAZY
    )
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    private UserProfile userProfile;


    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @PrePersist
    public void prePersist(){
        this.isEmailVerified= false;
    }
}
