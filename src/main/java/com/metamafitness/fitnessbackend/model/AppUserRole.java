package com.metamafitness.fitnessbackend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;


@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AppUserRole extends GenericEntity {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private GenericEnum.RoleName name;

}
