package com.metamafitness.fitnessbackend.dto;

import com.metamafitness.fitnessbackend.model.GenericEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhoneNumberDto {

    private GenericEnum.Region region;
    private String phoneNumber;
}
