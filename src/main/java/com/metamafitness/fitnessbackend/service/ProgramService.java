package com.metamafitness.fitnessbackend.service;

import com.metamafitness.fitnessbackend.model.Program;

import java.util.List;

public interface ProgramService extends GenericService<Program> {

    List<Program> findByCreator(Long id, int page, int size);

    Program patch(Program program);
}
