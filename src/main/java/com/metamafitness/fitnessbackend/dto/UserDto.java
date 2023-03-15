package com.metamafitness.fitnessbackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserDto extends AppUserDto{
    private String firstName;
    private String lastName;
    private String email;
    private String password;

}
