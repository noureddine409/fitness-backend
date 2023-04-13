package com.metamafitness.fitnessbackend.dto;

import com.metamafitness.fitnessbackend.model.GenericEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProgramSectionDto extends GenericDto {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private GenericEnum.SectionLevel level;

    private SectionVideoDto video;
}
