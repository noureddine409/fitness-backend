package com.metamafitness.fitnessbackend.dto;

import com.metamafitness.fitnessbackend.model.AppUserRole;
import com.metamafitness.fitnessbackend.model.GenericEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppUserDto extends GenericDto {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private GenericEnum.Gender gender;
    private Set<AppUserRole> roles;
}
