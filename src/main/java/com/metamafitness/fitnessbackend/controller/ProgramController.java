package com.metamafitness.fitnessbackend.controller;

import com.google.common.collect.ImmutableList;
import com.metamafitness.fitnessbackend.dto.ProgramDto;
import com.metamafitness.fitnessbackend.dto.ProgramPatchDto;
import com.metamafitness.fitnessbackend.exception.ResourceOwnershipException;
import com.metamafitness.fitnessbackend.model.Program;
import com.metamafitness.fitnessbackend.model.ProgramSection;
import com.metamafitness.fitnessbackend.model.SectionVideo;
import com.metamafitness.fitnessbackend.model.User;
import com.metamafitness.fitnessbackend.service.ProgramService;
import com.metamafitness.fitnessbackend.service.StorageService;
import com.metamafitness.fitnessbackend.validator.ValidVideoFiles;
import com.metamafitness.fitnessbackend.validator.validation.ProgramFileValidator;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.IntStream;

import static com.metamafitness.fitnessbackend.common.CoreConstant.Exception.AUTHORIZATION_RESOURCE_OWNERSHIP;
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

    @PatchMapping("/{id}")
    public ResponseEntity<ProgramDto> update(@PathVariable("id") Long id, @RequestBody ProgramPatchDto programDto) {
        final Program program = programService.findById(id);
        if(!isOwner(program)) {
            throw new ResourceOwnershipException(new ResourceOwnershipException(), AUTHORIZATION_RESOURCE_OWNERSHIP, null);
        }
        ModelMapper modelMapper = getModelMapper();

        // Save the original skipNullEnabled value
        boolean originalSkipNullEnabled = modelMapper.getConfiguration().isSkipNullEnabled();

        // Set skipNullEnabled to true for this mapping operation
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(programDto, program);

        // Set skipNullEnabled back to its original value
        modelMapper.getConfiguration().setSkipNullEnabled(originalSkipNullEnabled);

        Program updatedProgram = programService.update(id, program);

        return ResponseEntity.status(HttpStatus.OK).body(convertToDto(updatedProgram));

    }

    private boolean isOwner(Program programFound) {
        final Long currentUserId = getCurrentUserId();
        final Long resourceOwnerId = programFound.getCreatedBy().getId();
        return currentUserId.equals(resourceOwnerId);
    }

    @PostMapping
    public ResponseEntity<ProgramDto> save(@RequestPart(value = "program") @Valid ProgramDto programDto,
                                           @Valid @ValidVideoFiles @RequestPart(value = "files") List<MultipartFile> multipartFiles) {
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
