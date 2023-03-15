package com.metamafitness.fitnessbackend;

import com.metamafitness.fitnessbackend.model.AdminRole;
import com.metamafitness.fitnessbackend.model.TrainerRole;
import com.metamafitness.fitnessbackend.model.UserRole;
import com.metamafitness.fitnessbackend.service.AdminRoleService;
import com.metamafitness.fitnessbackend.service.TrainerRoleService;
import com.metamafitness.fitnessbackend.service.UserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class FitnessBackendApplication implements CommandLineRunner {

    private final UserRoleService userRoleService;

    final TrainerRoleService trainerRoleService;

    final AdminRoleService adminRoleService;

    public FitnessBackendApplication(UserRoleService userRoleService, TrainerRoleService trainerRoleService, AdminRoleService adminRoleService) {
        this.userRoleService = userRoleService;
        this.trainerRoleService = trainerRoleService;
        this.adminRoleService = adminRoleService;
    }

    public static void main(String[] args) {

        SpringApplication.run(FitnessBackendApplication.class, args);
    }


    @Override
    public void run(String... args) {
        userRoleService.save(new UserRole());
        trainerRoleService.save(new TrainerRole());
        adminRoleService.save(new AdminRole());
    }
}
