package com.metamafitness.fitnessbackend.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "trainer_request")
public class Join extends GenericEntity{
    @Column(nullable = false)
    private String expertise;

    @Column(nullable = false,columnDefinition = "text")
    private String message;

    @Column(nullable = false)
    private Boolean approved;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private User sender;

    @Column(nullable = false,columnDefinition = "text")
    private String experience;

    @ElementCollection(targetClass = String.class)
    private List<String> documents;

}
