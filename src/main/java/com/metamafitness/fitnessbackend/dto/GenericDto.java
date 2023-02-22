package com.metamafitness.fitnessbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GenericDto {

    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
