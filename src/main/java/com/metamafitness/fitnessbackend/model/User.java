package com.metamafitness.fitnessbackend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;




@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "users")
public class User extends GenericEntity {
    private String firstName;
    private String lastName;
    @Column(name = "email", nullable = false, length = 200)
    private String email;
    private String password;
}
