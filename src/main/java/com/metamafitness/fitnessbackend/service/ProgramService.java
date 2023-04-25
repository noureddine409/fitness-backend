package com.metamafitness.fitnessbackend.service;

import com.metamafitness.fitnessbackend.model.GenericEnum;
import com.metamafitness.fitnessbackend.model.Program;

import java.util.List;

public interface ProgramService extends GenericService<Program> {

    List<Program> findByCreator(Long id, int page, int size);

    long countByCreator(Long currentUserId);
    List<Program> findByCategory(GenericEnum.ProgramCategory category, int page, int size);
    long countByCategory(GenericEnum.ProgramCategory category);
}
