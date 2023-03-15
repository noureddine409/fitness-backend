package com.metamafitness.fitnessbackend.model;


import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Builder
@Getter @Setter
public class UserRole extends AppUserRole {

    public UserRole() {
        setName(GenericEnum.RoleName.USER);
    }
}