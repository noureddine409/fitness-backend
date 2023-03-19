package com.metamafitness.fitnessbackend.controller;


import com.metamafitness.fitnessbackend.common.CoreConstant;
import com.metamafitness.fitnessbackend.dto.GenericDto;
import com.metamafitness.fitnessbackend.exception.BusinessException;
import com.metamafitness.fitnessbackend.exception.ElementNotFoundException;
import com.metamafitness.fitnessbackend.model.GenericEntity;
import com.metamafitness.fitnessbackend.model.User;
import com.metamafitness.fitnessbackend.service.GenericService;
import com.metamafitness.fitnessbackend.service.UserService;
import com.metamafitness.fitnessbackend.utils.ClassTypeProvider;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class GenericController<T extends GenericEntity, D extends GenericDto> {
    final Logger LOG = LoggerFactory.getLogger(GenericController.class);

    @Autowired
    private GenericService<T> genericService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ClassTypeProvider classTypeProvider;

    @Autowired
    private UserService userService;

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

    protected Long getCurrentUserId() throws BusinessException {
        final Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        if(Objects.isNull(authentication.getPrincipal())){
            LOG.error(CoreConstant.Exception.AUTHENTICATION_NULL_PRINCIPAL);
            throw new BusinessException(null, new BusinessException(), CoreConstant.Exception.AUTHENTICATION_NULL_PRINCIPAL, null);
        }
        return (Long) authentication.getPrincipal();
    }

    protected User getCurrentUser() throws ElementNotFoundException {
        return userService.findById(getCurrentUserId());
    }


    @GetMapping("/{id}")

    public ResponseEntity<D> getById(@PathVariable("id") Long id) throws ElementNotFoundException {
        return new ResponseEntity<D>(convertToDto(genericService.findById(id)), HttpStatus.OK);
    }

}
