package com.metamafitness.fitnessbackend.controller;

import com.metamafitness.fitnessbackend.common.CoreConstant;
import com.metamafitness.fitnessbackend.dto.JoinDto;
import com.metamafitness.fitnessbackend.exception.BusinessException;
import com.metamafitness.fitnessbackend.model.GenericEnum;
import com.metamafitness.fitnessbackend.model.Join;
import com.metamafitness.fitnessbackend.model.TrainerRole;
import com.metamafitness.fitnessbackend.model.User;
import com.metamafitness.fitnessbackend.service.JoinService;
import com.metamafitness.fitnessbackend.service.TrainerRoleService;
import com.metamafitness.fitnessbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/join-us")
public class JoinController extends GenericController<Join, JoinDto> {

    private final JoinService joinService;
    private final UserService userService;

    private final TrainerRoleService trainerRoleService;


    public JoinController(JoinService joinService, UserService userService, TrainerRoleService trainerRoleService) {
        this.joinService = joinService;
        this.userService = userService;
        this.trainerRoleService = trainerRoleService;
    }

    @PostMapping
    public ResponseEntity<JoinDto> requestJoinAsTrainer(@RequestBody JoinDto joinDto) {
        final User user = getCurrentUser();
        Join join = convertToEntity(joinDto);
        join.setApproved(Boolean.FALSE);
        join.setSender(user);
        return new ResponseEntity<>(convertToDto(joinService.save(join)), HttpStatus.CREATED);
    }

    @PatchMapping("/accept/{id_request}")
    public ResponseEntity<JoinDto> acceptRequestJoinAsTrainer(@PathVariable("id_request") Long id)  {
        Join joinRequest = joinService.findById(id);
        if(joinRequest.getApproved())
            throw new BusinessException(new BusinessException(), CoreConstant.Exception.JOIN_REQUEST_ALREADY_HANDLED, null);
        final TrainerRole trainerRole = trainerRoleService.findByName(GenericEnum.RoleName.TRAINER);
        joinRequest.getSender().addRole(trainerRole);
        joinRequest.setApproved(Boolean.TRUE);
        Join accepted = joinService.update(id, joinRequest);
        return new ResponseEntity<>(convertToDto(accepted), HttpStatus.CREATED);
    }


}
