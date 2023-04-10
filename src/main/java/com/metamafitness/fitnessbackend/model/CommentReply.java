package com.metamafitness.fitnessbackend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CommentReply extends GenericEntity {

    private String reply;

    @ManyToOne
    private SectionComment comment;
}
