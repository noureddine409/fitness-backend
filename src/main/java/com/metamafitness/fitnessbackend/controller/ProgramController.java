package com.metamafitness.fitnessbackend.controller;

import com.metamafitness.fitnessbackend.dto.ProgramDto;
import com.metamafitness.fitnessbackend.model.Program;
import com.metamafitness.fitnessbackend.model.SectionVideo;
import com.metamafitness.fitnessbackend.model.User;
import com.metamafitness.fitnessbackend.service.ProgramService;
import com.metamafitness.fitnessbackend.service.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.metamafitness.fitnessbackend.model.GenericEnum.ProgramState;


@RestController
@RequestMapping("/api/programs")
public class ProgramController extends GenericController<Program, ProgramDto> {

    private final StorageService storageService;

    private final ProgramService programService;

    @Override
    public Program convertToEntity(ProgramDto dto) {
        final User currentUser = getCurrentUser();
        Program entity =  super.convertToEntity(dto);
        entity.setCreatedBy(currentUser);
        return entity;
    }

    public ProgramController(StorageService storageService, ProgramService programService) {
        this.storageService = storageService;
        this.programService = programService;
    }

    @PostMapping
    public ResponseEntity<ProgramDto> save(@RequestPart(value = "program", required = true) ProgramDto programDto,
                                           @RequestPart(value = "files", required = true) List<MultipartFile> multipartFiles) {

        Program programEntity = convertToEntity(programDto);

        List<String> paths = multipartFiles.stream()
                .map(storageService::save)
                .collect(Collectors.toList());

        IntStream.range(0, programEntity.getSections().size())
                .forEach(i -> programEntity.getSections().get(i).setVideo(
                        SectionVideo.builder().url(paths.get(i)).build()));

        programEntity.setState(ProgramState.IN_PROGRESS);

        ProgramDto savedProgramDto = convertToDto(programService.save(programEntity));
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProgramDto);
    }
}
