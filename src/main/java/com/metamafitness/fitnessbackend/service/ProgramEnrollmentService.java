package com.metamafitness.fitnessbackend.service;


import com.metamafitness.fitnessbackend.model.ProgramEnrollment;

public interface ProgramEnrollmentService extends GenericService<ProgramEnrollment> {

    ProgramEnrollment findByUserAndProgram(Long userId, Long programId);
}
