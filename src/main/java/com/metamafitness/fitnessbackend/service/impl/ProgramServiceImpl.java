package com.metamafitness.fitnessbackend.service.impl;

import com.metamafitness.fitnessbackend.exception.ElementNotFoundException;
import com.metamafitness.fitnessbackend.model.Program;
import com.metamafitness.fitnessbackend.repository.ProgramRepository;
import com.metamafitness.fitnessbackend.service.ProgramService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgramServiceImpl extends GenericServiceImpl<Program> implements ProgramService {

    private final ProgramRepository programRepository;



    public ProgramServiceImpl(ProgramRepository programRepository) {
        this.programRepository = programRepository;

    }

    @Override
    public List<Program> findByCreator(Long id, int page, int size) throws ElementNotFoundException {
        Pageable pageable = PageRequest.of(page, size);
        return programRepository.findByCreatedBy_id(id, pageable);
    }
}
