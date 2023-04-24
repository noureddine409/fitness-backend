package com.metamafitness.fitnessbackend.controller;

import com.metamafitness.fitnessbackend.dto.OrderDto;
import com.metamafitness.fitnessbackend.dto.ProgramEnrollmentDto;
import com.metamafitness.fitnessbackend.exception.ElementAlreadyExistException;
import com.metamafitness.fitnessbackend.exception.LinkNotFoundException;
import com.metamafitness.fitnessbackend.exception.TransactionAlreadyCompletedException;
import com.metamafitness.fitnessbackend.exception.UserAlreadyEnrolled;
import com.metamafitness.fitnessbackend.model.Program;
import com.metamafitness.fitnessbackend.model.ProgramEnrollment;
import com.metamafitness.fitnessbackend.service.PaymentService;
import com.metamafitness.fitnessbackend.service.ProgramEnrollmentService;
import com.metamafitness.fitnessbackend.service.ProgramService;
import com.paypal.orders.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Objects;

import static com.metamafitness.fitnessbackend.common.CoreConstant.Exception.APPROVAL_LINK_NOT_FOUND;
import static com.metamafitness.fitnessbackend.common.CoreConstant.Exception.AUTHORIZATION_USER_ALREADY_ENROLLED;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController extends GenericController<ProgramEnrollment, ProgramEnrollmentDto> {

    private final ProgramService programService;
    private final ProgramEnrollmentService programEnrollmentService;

    private final PaymentService paymentService;

    public EnrollmentController(ProgramService programService, ProgramEnrollmentService programEnrollmentService, PaymentService paymentService) {
        this.programService = programService;
        this.programEnrollmentService = programEnrollmentService;
        this.paymentService = paymentService;
    }

    @PostMapping("create-order/{programId}")
    ResponseEntity<OrderDto> createEnrollmentOrder(@PathVariable("programId") Long programId) throws IOException, ElementAlreadyExistException, UserAlreadyEnrolled {
        final Program program = programService.findById(programId);
        final Long currentUserId = getCurrentUserId();
        final ProgramEnrollment programEnrollment = programEnrollmentService.findByUserAndProgram(currentUserId, program.getId());
        if (Objects.nonNull(programEnrollment)) {
            throw new UserAlreadyEnrolled(new UserAlreadyEnrolled(), AUTHORIZATION_USER_ALREADY_ENROLLED, null);
        }
        Order order = paymentService.createOrder(program);

        String redirectUrl = order.links().stream()
                .filter(link -> "approve".equals(link.rel()))
                .findFirst()
                .orElseThrow(() -> new LinkNotFoundException(new LinkNotFoundException(), APPROVAL_LINK_NOT_FOUND, null))
                .href();

        return ResponseEntity
                .status(HttpStatus.CREATED).
                body(OrderDto
                        .builder()
                        .id(order.id())
                        .redirectUrl(redirectUrl)
                        .status(order.status())
                        .createdAt(order.createTime())
                        .build());
    }

    @PostMapping("/complete-order")
    public ResponseEntity<ProgramEnrollmentDto> completeOrder(@RequestParam String orderId) throws IOException, TransactionAlreadyCompletedException {
        ProgramEnrollment enrollment = paymentService.completeOrder(orderId, getCurrentUser());
        ProgramEnrollment saved = programEnrollmentService.save(enrollment);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(saved));
    }

}
