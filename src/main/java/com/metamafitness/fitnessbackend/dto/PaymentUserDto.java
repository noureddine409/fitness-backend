package com.metamafitness.fitnessbackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentUserDto {

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String payerId;
    private String shippingAddressLine1;
    private String shippingAddressLine2;
    private String shippingCity;
    private String shippingState;
    private String shippingCountryCode;
    private String shippingPostalCode;
}
