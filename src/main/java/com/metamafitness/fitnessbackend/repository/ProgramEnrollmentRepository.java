package com.metamafitness.fitnessbackend.repository;

import com.metamafitness.fitnessbackend.model.ProgramEnrollment;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProgramEnrollmentRepository extends GenericRepository<ProgramEnrollment>{

    Optional<ProgramEnrollment> findByUser_idAndProgram_id(Long userId, Long programId);
    boolean existsByPayment_transactionId(String transactionId);

    List<ProgramEnrollment> findByProgram_CreatedBy_id(Long id, Pageable pageable);

    Long countByProgram_CreatedBy_id(Long id);

}
