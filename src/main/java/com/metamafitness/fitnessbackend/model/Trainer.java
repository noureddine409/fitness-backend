package com.metamafitness.fitnessbackend.model;


import lombok.*;

import javax.persistence.Entity;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trainer extends AppUser{

    private boolean verified;
}
