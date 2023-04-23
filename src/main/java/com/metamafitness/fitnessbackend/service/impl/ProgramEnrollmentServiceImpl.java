package com.metamafitness.fitnessbackend.service.impl;


import com.metamafitness.fitnessbackend.model.ProgramEnrollment;
import com.metamafitness.fitnessbackend.repository.ProgramEnrollmentRepository;
import com.metamafitness.fitnessbackend.service.ProgramEnrollmentService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProgramEnrollmentServiceImpl extends GenericServiceImpl<ProgramEnrollment> implements ProgramEnrollmentService {
    private final ProgramEnrollmentRepository programEnrollmentRepository;

    public ProgramEnrollmentServiceImpl(ProgramEnrollmentRepository programEnrollmentRepository) {
        this.programEnrollmentRepository = programEnrollmentRepository;
    }

    @Override
    public ProgramEnrollment findByUserAndProgram(Long userId, Long programId) {
        Optional<ProgramEnrollment> enrollmentFound = programEnrollmentRepository.findByUser_idAndProgram_id(userId, programId);
        return enrollmentFound.orElse(null);
    }
}
