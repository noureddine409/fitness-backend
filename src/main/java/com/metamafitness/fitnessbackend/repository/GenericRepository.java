package com.metamafitness.fitnessbackend.repository;

import com.metamafitness.fitnessbackend.model.GenericEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GenericRepository <T extends GenericEntity> extends JpaRepository<T, Long> {
}
