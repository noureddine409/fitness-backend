package com.metamafitness.fitnessbackend.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class SectionComment extends GenericEntity {

    private String comment;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private ProgramSection section;

}
