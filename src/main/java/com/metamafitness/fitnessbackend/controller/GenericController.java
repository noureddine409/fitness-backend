package com.metamafitness.fitnessbackend.controller;


import com.metamafitness.fitnessbackend.dto.GenericDto;
import com.metamafitness.fitnessbackend.exception.ElementAlreadyExistException;
import com.metamafitness.fitnessbackend.exception.ElementNotFoundException;
import com.metamafitness.fitnessbackend.model.GenericEntity;
import com.metamafitness.fitnessbackend.service.GenericService;
import com.metamafitness.fitnessbackend.utils.ClassTypeProvider;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

public abstract class GenericController<T extends GenericEntity, D extends GenericDto> {
    final Logger LOG = LoggerFactory.getLogger(GenericController.class);

    @Autowired
    private GenericService<T> genericService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ClassTypeProvider classTypeProvider;

    public ModelMapper getModelMapper() {
        return modelMapper;
    }

    protected Class<?>[] getClasses() {
        return classTypeProvider.getClasses(this, GenericController.class);
    }

    public D convertToDto(T entity) {
        return (D) getModelMapper().map(entity, getClasses()[1]);
    }

    public T convertToEntity(D dto) {
        return (T) getModelMapper().map(dto, getClasses()[0]);
    }

    public T convertToUpdateEntity(D dto) {
        return convertToEntity(dto);
    }

    <T, D> List<D> convertListToDto(List<T> source, Class<D> targetClass) {
        return source
                .stream()
                .map(element -> modelMapper.map(element, targetClass))
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<D> save(@RequestBody D dto) throws ElementAlreadyExistException {
        T entity = convertToEntity(dto);
        T savedEntity = genericService.save(entity);
        return new ResponseEntity<D>(convertToDto(savedEntity), HttpStatus.CREATED);
    }

    @GetMapping("/")

    public ResponseEntity<D> getById(@PathVariable("id") Long id) throws ElementNotFoundException {
        return new ResponseEntity<D>(convertToDto(genericService.findById(id)), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<D> update(@PathVariable("id") Long id, @RequestBody D dto) throws ElementNotFoundException {
        dto.setId(id);
        T entity = genericService.update(id, convertToUpdateEntity(dto));
        return new ResponseEntity<>(convertToDto(entity), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable("id") Long id) throws ElementNotFoundException {
        return new ResponseEntity<Boolean>(genericService.delete(id), HttpStatus.OK);
    }

}
