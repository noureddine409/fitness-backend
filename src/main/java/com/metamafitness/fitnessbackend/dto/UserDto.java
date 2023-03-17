package com.metamafitness.fitnessbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.metamafitness.fitnessbackend.model.Address;
import com.metamafitness.fitnessbackend.model.AppUserRole;
import com.metamafitness.fitnessbackend.model.GenericEnum;
import com.metamafitness.fitnessbackend.model.PhoneNumber;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserDto extends GenericDto{
    private String firstName;
    private String lastName;

    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private LocalDate birthDay;

    private String profilePicture;

    private String verificationCode;

    private String bio;

    private boolean enabled;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String RefreshTokenId;

    private GenericEnum.Gender gender;


    private AddressDto address;


    private PhoneNumberDto phoneNumber;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<AppUserRoleDto> roles;

}
