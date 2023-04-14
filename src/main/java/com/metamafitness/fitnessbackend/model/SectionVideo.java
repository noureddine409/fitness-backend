package com.metamafitness.fitnessbackend.model;

import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionVideo {
    private String videoUrl;

    private String previewImageUrl;
    @OneToOne
    @JoinColumn(name = "section_id")
    private ProgramSection section;
}
