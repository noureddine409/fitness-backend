package com.metamafitness.fitnessbackend.repository;

import com.metamafitness.fitnessbackend.model.ProgramEnrollment;

import java.util.Optional;

public interface ProgramEnrollmentRepository extends GenericRepository<ProgramEnrollment>{

    Optional<ProgramEnrollment> findByUser_idAndProgram_id(Long userId, Long programId);
    boolean existsByPayment_transactionId(String transactionId);
}
