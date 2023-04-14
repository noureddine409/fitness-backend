package com.metamafitness.fitnessbackend.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramReview extends GenericEntity {

    private String review;

    @ManyToOne
    private Program program;
}
