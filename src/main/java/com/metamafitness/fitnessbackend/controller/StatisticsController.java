package com.metamafitness.fitnessbackend.controller;

import com.metamafitness.fitnessbackend.dto.GenericDto;
import com.metamafitness.fitnessbackend.dto.StatisticsDto;
import com.metamafitness.fitnessbackend.model.GenericEntity;
import com.metamafitness.fitnessbackend.service.ProgramEnrollmentService;
import com.metamafitness.fitnessbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController extends GenericController<GenericEntity, GenericDto> {

    private final ProgramEnrollmentService enrollmentService;
    private final UserService userService;

    public StatisticsController(ProgramEnrollmentService enrollmentService, UserService userService) {
        this.enrollmentService = enrollmentService;
        this.userService = userService;
    }

    @GetMapping("/trainer")
    public ResponseEntity<StatisticsDto> getTrainerStatistics() {
        final Long currentUserId = getCurrentUserId();
        final BigDecimal totalProfit = enrollmentService.getTotalProfitByTrainer(currentUserId);
        final Long totalSales = enrollmentService.countByProgramCreator(currentUserId);
        final Long totalUsers = userService.countNewEnrolledUsers(currentUserId);

        return ResponseEntity.status(HttpStatus.OK).body(
                StatisticsDto.builder()
                        .totalProfit(totalProfit)
                        .newSales(totalSales)
                        .newUsers(totalUsers)
                        .build()
        );
    }
}
