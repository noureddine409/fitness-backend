package com.metamafitness.fitnessbackend.dto;

import com.metamafitness.fitnessbackend.model.GenericEnum;
import com.metamafitness.fitnessbackend.validator.ValidPhoneNumber;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class UserPatchDto {

    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private LocalDate birthDay;
    private String bio;
    @NotNull
    private GenericEnum.Gender gender;
    private AddressDto address;
    @ValidPhoneNumber
    private PhoneNumberDto phoneNumber;

    private SocialMediaDto socialMedia;
}
