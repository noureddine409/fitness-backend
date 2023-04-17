package com.metamafitness.fitnessbackend.service.impl;

import com.metamafitness.fitnessbackend.common.CoreConstant;
import com.metamafitness.fitnessbackend.exception.BusinessException;
import com.metamafitness.fitnessbackend.exception.ElementAlreadyExistException;
import com.metamafitness.fitnessbackend.exception.ElementNotFoundException;
import com.metamafitness.fitnessbackend.model.GenericEntity;
import com.metamafitness.fitnessbackend.repository.GenericRepository;
import com.metamafitness.fitnessbackend.service.GenericService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GenericServiceImpl<T extends GenericEntity> implements GenericService<T> {
    final Logger LOG = LoggerFactory.getLogger(GenericServiceImpl.class);
    @Autowired
    private GenericRepository<T> genericRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public T update(Long id, T entity) throws ElementNotFoundException {
        final Optional<T> foundEntity = genericRepository.findById(id);

        if (foundEntity.isEmpty()) {
            LOG.warn(CoreConstant.Exception.NOT_FOUND);
            throw new ElementNotFoundException(null, new ElementNotFoundException(), CoreConstant.Exception.NOT_FOUND, new Object[]{id});
        }
        T newEntity = foundEntity.get();
        entity.setCreatedAt(newEntity.getCreatedAt());
        modelMapper.map(entity, newEntity);
        newEntity.setId(id);
        newEntity.setUpdatedAt(LocalDateTime.now());

        return genericRepository.save(newEntity);
    }

    @Override
    public List<T> search(String keyword, Pageable pageable) throws BusinessException {
        try {
            return genericRepository.searchByKeyword(keyword, pageable).toList();
        } catch (BusinessException e) {
            throw new BusinessException(null, e, CoreConstant.Exception.FIND_ELEMENTS, null);

        }

    }

    @Override
    public T findById(Long id) throws ElementNotFoundException {
        final Optional<T> entityExist = genericRepository.findById(id);
        if (entityExist.isPresent()) {
            return entityExist.get();
        } else {
            LOG.warn(CoreConstant.Exception.NOT_FOUND);
            throw new ElementNotFoundException(null, new ElementNotFoundException(), CoreConstant.Exception.NOT_FOUND, new Object[]{id});
        }
    }

    @Override
    public T save(T entity) throws ElementAlreadyExistException {
        final Long id = entity.getId();
        if (id == null)
            return genericRepository.save(entity);
        final Optional<T> entityExist = genericRepository.findById(id);
        if (entityExist.isEmpty()) {
            return genericRepository.save(entity);
        } else {
            LOG.warn(CoreConstant.Exception.ALREADY_EXISTS);
            throw new ElementAlreadyExistException(null, new ElementAlreadyExistException(), CoreConstant.Exception.ALREADY_EXISTS, new Object[]{id});
        }
    }

    @Override
    public boolean delete(Long id) throws ElementNotFoundException {
        try {
            genericRepository.deleteById(id);
            return true;
        } catch (final BusinessException e) {
            LOG.error("Error", e);
            throw new BusinessException(null, e, CoreConstant.Exception.DELETE_ELEMENT, new Object[]{id});
        }
    }

    @Override
    public List<T> findAll() throws BusinessException {
        try {
            return genericRepository.findAll();
        } catch (BusinessException e) {
            throw new BusinessException(null, e, CoreConstant.Exception.FIND_ELEMENTS, null);
        }
    }

    @Override
    public T patch(T entity) {
        entity.setUpdatedAt(LocalDateTime.now());
        return genericRepository.save(entity);
    }


}
