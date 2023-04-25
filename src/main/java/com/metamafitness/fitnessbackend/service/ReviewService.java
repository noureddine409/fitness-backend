package com.metamafitness.fitnessbackend.service;

import com.metamafitness.fitnessbackend.model.ProgramReview;

public interface ReviewService extends GenericService<ProgramReview> {

    Boolean findByUserAndProgram(Long userId, Long ProgramId);
}
