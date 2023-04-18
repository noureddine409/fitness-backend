package com.metamafitness.fitnessbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static com.metamafitness.fitnessbackend.model.GenericEnum.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProgramDto extends GenericDto {

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotNull
    private ProgramLevel level;

    private String picture;
    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal price;

    @NotNull
    private ProgramCategory category;

    @NotBlank
    private String description;

    private UserDto createdBy;

    @Size(max = 255)
    private String motivationDescription;

    @NotNull
    @Positive
    private int durationPerDay; // in minutes


    private Set<ProgramOption> options;


    private Set<ProgramEquipment> equipments;

    @NotEmpty
    @Valid
    private List<ProgramSectionDto> sections;
}
