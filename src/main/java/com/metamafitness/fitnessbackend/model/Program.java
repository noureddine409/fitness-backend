package com.metamafitness.fitnessbackend.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.metamafitness.fitnessbackend.model.GenericEnum.ProgramCategory;
import static com.metamafitness.fitnessbackend.model.GenericEnum.ProgramState;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
public class Program extends GenericEntity {

    private String name;

    private String picture;

    private BigDecimal price;

    private String level;

    @Enumerated(EnumType.STRING)
    private ProgramState state;

    @Enumerated(EnumType.STRING)
    private ProgramCategory category;

    private String description;

    private String motivationDescription;

    private int durationPerDay; // in minutes

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @ElementCollection
    private Set<String> options;

    @ElementCollection
    private Set<String> equipments;

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL)
    private List<ProgramSection> sections;

    @OneToMany(mappedBy = "program", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<ProgramReview> reviews = new ArrayList<>();
}
