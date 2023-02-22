package com.metamafitness.fitnessbackend.repository;

import com.metamafitness.fitnessbackend.model.GenericEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;


public interface GenericRepository <T extends GenericEntity> extends JpaRepository<T, Long> {
}
