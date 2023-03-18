package com.metamafitness.fitnessbackend.dto;

import com.metamafitness.fitnessbackend.common.CoreConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

    private String country;
    private String city;

    @Pattern(regexp = CoreConstant.Validation.POSTAL_CODE_REGEX)
    private String postalCode;


}
