package com.metamafitness.fitnessbackend.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoinDto extends GenericDto{
    @NotNull
    private String expertise;
    @NotNull
    private String message;
    private Boolean approved;
    private UserDto sender;
    private String experience;
    private List<String> documents;
}
