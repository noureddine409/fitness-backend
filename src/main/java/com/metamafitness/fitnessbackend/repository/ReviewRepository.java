package com.metamafitness.fitnessbackend.repository;

import com.metamafitness.fitnessbackend.model.ProgramReview;

public interface ReviewRepository extends GenericRepository<ProgramReview>{

    Boolean existsByCreatedBy_idAndProgram_id(Long userId, Long programId);


}
