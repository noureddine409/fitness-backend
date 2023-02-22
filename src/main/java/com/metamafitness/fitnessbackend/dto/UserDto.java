package com.metamafitness.fitnessbackend.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserDto extends GenericDto{
    private String firstName;
    private String lastName;
    private String email;
    private String password;

}
