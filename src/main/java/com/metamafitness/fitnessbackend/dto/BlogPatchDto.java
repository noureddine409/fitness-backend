package com.metamafitness.fitnessbackend.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogPatchDto {
    @Size(max = 255)
    private String name;
    private String description;
    @NotEmpty
    private Set<String> tags;
}
