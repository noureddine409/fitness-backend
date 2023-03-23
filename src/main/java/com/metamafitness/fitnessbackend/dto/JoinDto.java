package com.metamafitness.fitnessbackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoinDto extends GenericDto{

    private String expertise;


    private String message;


    private Boolean approved;


    private UserDto sender;


    private String experience;
}
