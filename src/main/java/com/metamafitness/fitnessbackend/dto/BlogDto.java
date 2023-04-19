package com.metamafitness.fitnessbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlogDto extends GenericDto{
    @NotBlank
    @Size(max = 255)
    private String name;
    private String picture;
    @NotBlank
    @Size(max = 255)
    private String description;
    private UserDto createdBy;
    @NotEmpty
    private Set<String> tags;
}
