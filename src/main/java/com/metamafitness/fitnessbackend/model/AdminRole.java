package com.metamafitness.fitnessbackend.model;

import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Builder
public class AdminRole extends AppUserRole {

    public AdminRole() {
        setName(GenericEnum.RoleName.ADMIN);
    }
}