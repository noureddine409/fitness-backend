package com.metamafitness.fitnessbackend.dto;

import com.metamafitness.fitnessbackend.model.GenericEnum;
import com.metamafitness.fitnessbackend.model.SectionVideo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProgramSectionDto extends GenericDto{

    private String title;

    private String description;

    private GenericEnum.SectionLevel level;

    private SectionVideoDto video;
}
