package com.metamafitness.fitnessbackend.service.impl;

import com.metamafitness.fitnessbackend.model.ProgramReview;
import com.metamafitness.fitnessbackend.repository.GenericRepository;
import com.metamafitness.fitnessbackend.service.ReviewService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl extends GenericServiceImpl<ProgramReview> implements ReviewService {
    public ReviewServiceImpl(GenericRepository<ProgramReview> genericRepository, ModelMapper modelMapper) {
        super(genericRepository, modelMapper);
    }
}