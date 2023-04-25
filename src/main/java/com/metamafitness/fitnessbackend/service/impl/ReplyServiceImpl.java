package com.metamafitness.fitnessbackend.service.impl;

import com.metamafitness.fitnessbackend.exception.ElementNotFoundException;
import com.metamafitness.fitnessbackend.model.CommentReply;
import com.metamafitness.fitnessbackend.service.ReplyService;
import org.springframework.stereotype.Service;

@Service
public class ReplyServiceImpl extends GenericServiceImpl<CommentReply> implements ReplyService {

    @Override
    public CommentReply findById(Long id) throws ElementNotFoundException {
        return super.findById(id);
    }
}
