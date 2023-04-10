package com.metamafitness.fitnessbackend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SectionComment extends GenericEntity {

    private String comment;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private ProgramSection section;

}
