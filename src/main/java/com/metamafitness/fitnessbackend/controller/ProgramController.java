package com.metamafitness.fitnessbackend.controller;

import com.google.common.collect.ImmutableList;
import com.metamafitness.fitnessbackend.dto.ProgramDto;
import com.metamafitness.fitnessbackend.dto.ProgramPatchDto;
import com.metamafitness.fitnessbackend.exception.ResourceDeletionNotAllowedException;
import com.metamafitness.fitnessbackend.exception.ResourceOwnershipException;
import com.metamafitness.fitnessbackend.model.Program;
import com.metamafitness.fitnessbackend.model.ProgramSection;
import com.metamafitness.fitnessbackend.model.SectionVideo;
import com.metamafitness.fitnessbackend.model.User;
import com.metamafitness.fitnessbackend.service.ProgramService;
import com.metamafitness.fitnessbackend.service.StorageService;
import com.metamafitness.fitnessbackend.service.UserService;
import com.metamafitness.fitnessbackend.validator.ValidPicture;
import com.metamafitness.fitnessbackend.validator.ValidPreviewPictures;
import com.metamafitness.fitnessbackend.validator.ValidVideoFiles;
import com.metamafitness.fitnessbackend.validator.validation.ProgramFileValidator;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.metamafitness.fitnessbackend.common.CoreConstant.Exception.AUTHORIZATION_RESOURCE_DELETION_NOT_ALLOWED;
import static com.metamafitness.fitnessbackend.common.CoreConstant.Exception.AUTHORIZATION_RESOURCE_OWNERSHIP;
import static com.metamafitness.fitnessbackend.common.CoreConstant.Pagination.DEFAULT_PAGE_NUMBER;
import static com.metamafitness.fitnessbackend.common.CoreConstant.Pagination.DEFAULT_PAGE_SIZE;
import static com.metamafitness.fitnessbackend.model.GenericEnum.ProgramState;


@RestController
@RequestMapping("/api/programs")
@Validated
public class ProgramController extends GenericController<Program, ProgramDto> {
    private final ProgramFileValidator programFileValidator;
    private final StorageService storageService;

    private final ProgramService programService;

    private final UserService userService;

    @Override
    public Program convertToEntity(ProgramDto dto) {
        final User currentUser = getCurrentUser();
        Program entity = super.convertToEntity(dto);
        entity.setCreatedBy(currentUser);
        return entity;
    }

    public ProgramController(StorageService storageService, ProgramService programService, ProgramFileValidator programFileValidator, UserService userService) {
        this.storageService = storageService;
        this.programService = programService;
        this.programFileValidator = programFileValidator;
        this.userService = userService;

    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProgramDto> update(@PathVariable("id") Long id, @RequestBody ProgramPatchDto programDto) {
        final Program program = programService.findById(id);
        if (isNotOwner(program)) {
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

        Program updatedProgram = programService.patch(program);

        return ResponseEntity.status(HttpStatus.OK).body(convertToDto(updatedProgram));

    }

    @GetMapping("/me")
    public ResponseEntity<List<ProgramDto>> findTrainerPrograms(@RequestParam(value = "page", defaultValue = "" + DEFAULT_PAGE_NUMBER) Integer page,
                                                                @RequestParam(value = "size", defaultValue = "" + DEFAULT_PAGE_SIZE) Integer size) {
        Long currentUserId = getCurrentUserId();

        List<Program> programs = programService.findByCreator(currentUserId, page, size);

        List<ProgramDto> dto = programs.stream().map(this::convertToDto).collect(Collectors.toList());

        long totalPrograms = programService.countByCreator(currentUserId);
        int totalPages = (int) Math.ceil((double) totalPrograms / size);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Pages", String.valueOf(totalPages));
        headers.add("Access-Control-Expose-Headers", "X-Total-Pages");

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(dto);

    }

    @GetMapping("/trainers/{trainerId}")
    public ResponseEntity<List<ProgramDto>> findTrainerPrograms(@PathVariable("trainerId") Long id,
                                                                @RequestParam(value = "page", defaultValue = "" + DEFAULT_PAGE_NUMBER) Integer page,
                                                                @RequestParam(value = "size", defaultValue = "" + DEFAULT_PAGE_SIZE) Integer size) {
        User userFound = userService.findById(id);

        List<Program> programs = programService.findByCreator(userFound.getId(), page, size);

        List<ProgramDto> dto = programs.stream().map(this::convertToDto).collect(Collectors.toList());
        long totalPrograms = programService.countByCreator(id);
        int totalPages = (int) Math.ceil((double) totalPrograms / size);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Pages", String.valueOf(totalPages));
        headers.add("Access-Control-Expose-Headers", "X-Total-Pages");

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(dto);
    }

    @PatchMapping("/{id}/submit")
    public ResponseEntity<ProgramDto> submit(@PathVariable("id") Long id) {
        final Program program = programService.findById(id);
        if (isNotOwner(program)) {
            throw new ResourceOwnershipException(new ResourceOwnershipException(), AUTHORIZATION_RESOURCE_OWNERSHIP, null);
        }

        program.setState(ProgramState.SUBMITTED);

        Program submittedProgram = programService.update(id, program);

        return ResponseEntity.status(HttpStatus.OK).body(convertToDto(submittedProgram));
    }

    @PatchMapping("/{id}/validate")
    public ResponseEntity<ProgramDto> validate(@PathVariable("id") Long id) {
        final Program program = programService.findById(id);

        program.setState(ProgramState.APPROVED);

        Program submittedProgram = programService.update(id, program);

        return ResponseEntity.status(HttpStatus.OK).body(convertToDto(submittedProgram));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable("id") Long id) {
        final Program program = programService.findById(id);
        if (isNotOwner(program)) {
            throw new ResourceOwnershipException(new ResourceOwnershipException(), AUTHORIZATION_RESOURCE_OWNERSHIP, null);
        }
        if (!ProgramState.IN_PROGRESS.equals(program.getState())) {
            throw new ResourceDeletionNotAllowedException(new ResourceDeletionNotAllowedException(), AUTHORIZATION_RESOURCE_DELETION_NOT_ALLOWED, null);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(programService.delete(id));
    }

    private boolean isNotOwner(Program programFound) {
        final Long currentUserId = getCurrentUserId();
        final Long resourceOwnerId = programFound.getCreatedBy().getId();
        return !currentUserId.equals(resourceOwnerId);
    }

    @PostMapping
    public ResponseEntity<ProgramDto> save(@RequestPart(value = "program") @Valid ProgramDto programDto, @Valid @ValidVideoFiles @RequestPart(value = "section-videos") List<MultipartFile> videos, @Valid @ValidPreviewPictures @RequestPart(value = "section-pictures") List<MultipartFile> previewPictures, @Valid @ValidPicture @RequestPart(value = "program-picture") MultipartFile picture) {

        programFileValidator.validate(programDto, videos, previewPictures);
        Program programEntity = convertToEntity(programDto);
        List<String> videoUrls = storageService.storeFiles(videos);
        List<String> previewImageUrls = storageService.storeFiles(previewPictures);
        String pictureUrl = storageService.save(picture);

        Program savedProgram = createProgram(programEntity, videoUrls, previewImageUrls, pictureUrl);

        ProgramDto savedProgramDto = convertToDto(savedProgram);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProgramDto);
    }

    private Program createProgram(Program programEntity, List<String> videoUrls, List<String> previewImageUrls, String pictureUrl) {
        ImmutableList<ProgramSection> sections = ImmutableList.copyOf(programEntity.getSections());
        IntStream.range(0, sections.size()).forEachOrdered(i -> {
            ProgramSection section = sections.get(i);
            section.setProgram(programEntity);
            section.setVideo(SectionVideo.builder().previewImageUrl(previewImageUrls.get(i)).videoUrl(videoUrls.get(i)).build());
        });
        programEntity.setState(ProgramState.IN_PROGRESS);
        programEntity.setPicture(pictureUrl);

        return programService.save(programEntity);
    }

}
