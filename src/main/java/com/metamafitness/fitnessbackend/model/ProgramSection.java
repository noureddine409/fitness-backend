package com.metamafitness.fitnessbackend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProgramSection extends GenericEntity {

    private String title;

    private String description;
    @Enumerated(EnumType.STRING)
    private GenericEnum.SectionLevel level;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "program_id")
    private Program program;

    @Embedded
    private SectionVideo video;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SectionComment> comments = new ArrayList<>();
}
