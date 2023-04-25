package com.metamafitness.fitnessbackend.controller;

import com.metamafitness.fitnessbackend.dto.ProgramReviewDto;
import com.metamafitness.fitnessbackend.dto.ReviewPatchDto;
import com.metamafitness.fitnessbackend.exception.ResourceOwnershipException;
import com.metamafitness.fitnessbackend.model.Program;
import com.metamafitness.fitnessbackend.model.ProgramReview;
import com.metamafitness.fitnessbackend.model.User;
import com.metamafitness.fitnessbackend.service.ProgramService;
import com.metamafitness.fitnessbackend.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.metamafitness.fitnessbackend.common.CoreConstant.Exception.AUTHORIZATION_RESOURCE_OWNERSHIP;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController extends GenericController<ProgramReview, ProgramReviewDto>{


    private final ProgramService programService;

    private final ReviewService reviewService;

    public ReviewController(ProgramService programService, ReviewService reviewService) {
        this.programService = programService;
        this.reviewService = reviewService;
    }

    @PostMapping("/{programId}")
    public ResponseEntity<ProgramReviewDto> addReview(@PathVariable Long programId, @RequestBody ProgramReviewDto reviewDto) {
        Program program = programService.findById(programId);
        User currentUser = getCurrentUser();
        ProgramReview programReview = convertToEntity(reviewDto);
        programReview.setProgram(program);
        programReview.setCreatedBy(currentUser);

        ProgramReview saved = reviewService.save(programReview);

        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(saved));
    }

    @PatchMapping("/{reviewId}")
    public ResponseEntity<ProgramReviewDto> update(@PathVariable Long reviewId, @RequestBody ReviewPatchDto patchDto) {
        ProgramReview review = reviewService.findById(reviewId);
        if(isNotOwner(review)) {
            throw new ResourceOwnershipException(new ResourceOwnershipException(), AUTHORIZATION_RESOURCE_OWNERSHIP, null);
        }
        mapWithSkipNull(patchDto, review);
        ProgramReview updatedReview = reviewService.patch(review);

        return ResponseEntity.status(HttpStatus.OK).body(convertToDto(updatedReview));
    }

    private boolean isNotOwner(ProgramReview review) {
        final Long currentUserId = getCurrentUserId();
        final Long resourceOwnerId = review.getCreatedBy().getId();
        return !currentUserId.equals(resourceOwnerId);
    }


}
