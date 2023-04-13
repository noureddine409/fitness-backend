package com.metamafitness.fitnessbackend.controller;

import com.google.common.collect.ImmutableList;
import com.metamafitness.fitnessbackend.dto.ProgramDto;
import com.metamafitness.fitnessbackend.model.Program;
import com.metamafitness.fitnessbackend.model.ProgramSection;
import com.metamafitness.fitnessbackend.model.SectionVideo;
import com.metamafitness.fitnessbackend.model.User;
import com.metamafitness.fitnessbackend.service.ProgramService;
import com.metamafitness.fitnessbackend.service.StorageService;
import com.metamafitness.fitnessbackend.validator.ValidVideoFiles;
import com.metamafitness.fitnessbackend.validator.validation.ProgramFileValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.IntStream;

import static com.metamafitness.fitnessbackend.model.GenericEnum.ProgramState;


@RestController
@RequestMapping("/api/programs")
@Validated
public class ProgramController extends GenericController<Program, ProgramDto> {
    private final ProgramFileValidator programFileValidator;
    private final StorageService storageService;

    private final ProgramService programService;

    @Override
    public Program convertToEntity(ProgramDto dto) {
        final User currentUser = getCurrentUser();
        Program entity = super.convertToEntity(dto);
        entity.setCreatedBy(currentUser);
        return entity;
    }

    public ProgramController(StorageService storageService, ProgramService programService, ProgramFileValidator programFileValidator) {
        this.storageService = storageService;
        this.programService = programService;
        this.programFileValidator = programFileValidator;
    }

    @PostMapping
    public ResponseEntity<ProgramDto> save(@RequestPart(value = "program", required = true) @Valid ProgramDto programDto,
                                           @Valid @ValidVideoFiles @RequestPart(value = "files", required = true) List<MultipartFile> multipartFiles) {
        programFileValidator.validate(programDto, multipartFiles);
        Program programEntity = convertToEntity(programDto);
        List<String> videoUrls = storageService.storeFiles(multipartFiles);

        Program savedProgram = createProgram(programEntity, videoUrls);

        ProgramDto savedProgramDto = convertToDto(savedProgram);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProgramDto);
    }

    private Program createProgram(Program programEntity, List<String> videoUrls) {
        ImmutableList<ProgramSection> sections = ImmutableList.copyOf(programEntity.getSections());
        IntStream.range(0, sections.size()).forEachOrdered(i -> sections.get(i).setVideo(
                SectionVideo.builder().url(videoUrls.get(i)).build()));
        programEntity.setState(ProgramState.IN_PROGRESS);
        return programService.save(programEntity);
    }

}
