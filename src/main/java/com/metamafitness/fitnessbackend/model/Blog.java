package com.metamafitness.fitnessbackend.model;

import com.metamafitness.fitnessbackend.model.GenericEnum.BlogState;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
public class Blog extends GenericEntity {
    private String name;

    private String picture;
    @Enumerated(EnumType.STRING)
    private BlogState state;
    private String description;
    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "created_by_id")
    private User createdBy;
    @ElementCollection(targetClass = String.class)
    private Set<String> tags;

}
