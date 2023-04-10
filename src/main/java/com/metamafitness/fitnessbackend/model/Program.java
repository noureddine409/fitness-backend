package com.metamafitness.fitnessbackend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.metamafitness.fitnessbackend.model.GenericEnum.ProgramEquipment;
import static com.metamafitness.fitnessbackend.model.GenericEnum.ProgramOption;
import static com.metamafitness.fitnessbackend.model.GenericEnum.ProgramCategory;
import static com.metamafitness.fitnessbackend.model.GenericEnum.ProgramLevel;
import static com.metamafitness.fitnessbackend.model.GenericEnum.ProgramState;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Program extends GenericEntity {

    private String name;

    private String picture;

    @Enumerated(EnumType.STRING)
    private ProgramLevel level;

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

    @ElementCollection(targetClass = ProgramOption.class)
    @Enumerated(EnumType.STRING)
    private Set<ProgramOption> options;

    @ElementCollection(targetClass = ProgramEquipment.class)
    @Enumerated(EnumType.STRING)
    private Set<ProgramEquipment> equipments;

    @OneToMany(mappedBy = "program", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<ProgramSection> sections = new ArrayList<>();

    @OneToMany(mappedBy = "program", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<ProgramReview> reviews = new ArrayList<>();
}
