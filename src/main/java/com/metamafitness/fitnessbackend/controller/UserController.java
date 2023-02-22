package com.metamafitness.fitnessbackend.controller;

import com.metamafitness.fitnessbackend.dto.UserDto;
import com.metamafitness.fitnessbackend.exception.ElementAlreadyExistException;
import com.metamafitness.fitnessbackend.exception.ElementNotFoundException;
import com.metamafitness.fitnessbackend.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController extends GenericController<User, UserDto>{

    @Override
    public ModelMapper getModelMapper() {
        return super.getModelMapper();
    }

    @Override
    @PostMapping
    public ResponseEntity<UserDto> save(@RequestBody UserDto dto) throws ElementAlreadyExistException {
        return super.save(dto);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable("id") Long id) throws ElementNotFoundException {
        return super.getById(id);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable("id") Long id, @RequestBody UserDto dto) throws ElementNotFoundException {
        return super.update(id, dto);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable("id") Long id) throws ElementNotFoundException {
        return super.delete(id);
    }
}
