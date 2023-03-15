package com.metamafitness.fitnessbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public abstract class AppUser extends GenericEntity {
    private String firstName;
    private String lastName;
    @Column(name = "email", nullable = false, length = 200)
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private GenericEnum.Gender gender;

    @ManyToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<AppUserRole> roles;

    public void addRole(AppUserRole role) {
        if (roles == null) {
            roles = new HashSet<>();
        }
        roles.add(role);
    }
}
