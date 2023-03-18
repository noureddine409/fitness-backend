package com.metamafitness.fitnessbackend.controller;

import com.metamafitness.fitnessbackend.dto.UserDto;
import com.metamafitness.fitnessbackend.exception.ElementNotFoundException;
import com.metamafitness.fitnessbackend.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController extends GenericController<User, UserDto>{

    @Override
    public ModelMapper getModelMapper() {
        return super.getModelMapper();
    }
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable("id") Long id) throws ElementNotFoundException {
        return super.getById(id);
    }


}
