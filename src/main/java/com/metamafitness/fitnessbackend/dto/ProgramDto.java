package com.metamafitness.fitnessbackend.dto;


import com.metamafitness.fitnessbackend.model.ProgramReview;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.metamafitness.fitnessbackend.model.GenericEnum.ProgramEquipment;
import static com.metamafitness.fitnessbackend.model.GenericEnum.ProgramOption;
import static com.metamafitness.fitnessbackend.model.GenericEnum.ProgramCategory;
import static com.metamafitness.fitnessbackend.model.GenericEnum.ProgramLevel;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProgramDto extends GenericDto {

    private String name;

    private ProgramLevel level;

    private BigDecimal price;

    private ProgramCategory category;

    private String description;

    private UserDto createdBy;

    private String motivationDescription;

    private int durationPerDay; // in minutes


    private Set<ProgramOption> options;

    private Set<ProgramEquipment> equipments;

    private List<ProgramSectionDto> sections = new ArrayList<>();
}
