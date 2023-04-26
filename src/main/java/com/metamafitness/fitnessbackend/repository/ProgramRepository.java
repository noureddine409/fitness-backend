package com.metamafitness.fitnessbackend.repository;

import com.metamafitness.fitnessbackend.model.GenericEnum;
import com.metamafitness.fitnessbackend.model.Program;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProgramRepository extends GenericRepository<Program>{
    List<Program> findByCreatedBy_id(Long id, Pageable pageable);

    long countByCreatedBy_id(Long id);
    List<Program> findByCategory(GenericEnum.ProgramCategory category, Pageable pageable);
    long countByCategory(GenericEnum.ProgramCategory category);


}
