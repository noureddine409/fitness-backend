package com.metamafitness.fitnessbackend.service.impl;

import com.metamafitness.fitnessbackend.common.CoreConstant;
import com.metamafitness.fitnessbackend.exception.ElementAlreadyExistException;
import com.metamafitness.fitnessbackend.model.Join;
import com.metamafitness.fitnessbackend.repository.JoinRepository;
import com.metamafitness.fitnessbackend.service.JoinService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JoinServiceImpl extends GenericServiceImpl<Join> implements JoinService {

    private final JoinRepository joinRepository;

    public JoinServiceImpl(JoinRepository joinRepository) {
        this.joinRepository = joinRepository;
    }

    @Override
    public Join save(Join entity) throws ElementAlreadyExistException {
        final Long senderId = entity.getSender().getId();
        final Optional<Join> entityExist = joinRepository.findBySender_id(senderId);

        if (entityExist.isEmpty()) {
            return joinRepository.save(entity);
        } else {
            LOG.warn(CoreConstant.Exception.ALREADY_EXISTS);
            throw new ElementAlreadyExistException(null, new ElementAlreadyExistException(), CoreConstant.Exception.ALREADY_EXISTS, new Object[]{});
        }
    }


}
