package com.metamafitness.fitnessbackend.service.impl;

import com.metamafitness.fitnessbackend.exception.ElementNotFoundException;
import com.metamafitness.fitnessbackend.model.SectionComment;
import com.metamafitness.fitnessbackend.service.CommentService;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl extends GenericServiceImpl<SectionComment> implements CommentService {

    @Override
    public SectionComment findById(Long id) throws ElementNotFoundException {
        return super.findById(id);
    }
}
