package com.metamafitness.fitnessbackend.dto;


import com.metamafitness.fitnessbackend.common.CoreConstant;
import com.metamafitness.fitnessbackend.validator.ValidPassword;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class UserRegisterDto {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @ValidPassword
    private String password;
}
