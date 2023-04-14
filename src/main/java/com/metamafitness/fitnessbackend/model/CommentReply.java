package com.metamafitness.fitnessbackend.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class CommentReply extends GenericEntity {

    private String reply;

    @ManyToOne
    private SectionComment comment;
}
