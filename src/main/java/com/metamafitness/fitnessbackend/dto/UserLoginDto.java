package com.metamafitness.fitnessbackend.dto;


import com.metamafitness.fitnessbackend.common.CoreConstant;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class UserLoginDto {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Length(min = CoreConstant.Validation.PASSWORD_SIZE_MIN, max = CoreConstant.Validation.PASSWORD_SIZE_MAX)
    private String password;
}