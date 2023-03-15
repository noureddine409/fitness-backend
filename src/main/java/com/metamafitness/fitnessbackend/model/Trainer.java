package com.metamafitness.fitnessbackend.model;

import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trainer extends AppUser{

    private boolean verified;
}
