package com.ga.airticketmanagement.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@ToString(exclude = {"password"})
public class User {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column
   @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @OneToOne(
        cascade = CascadeType.ALL, fetch =FetchType.LAZY
    )
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    private UserProfile userProfile;


    @JsonIgnore
    public String getPassword() {
        return password;
    }
}
