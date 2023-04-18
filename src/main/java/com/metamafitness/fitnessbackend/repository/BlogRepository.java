package com.metamafitness.fitnessbackend.repository;

import com.metamafitness.fitnessbackend.model.Blog;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BlogRepository extends GenericRepository<Blog>{
    List<Blog> findByCreatedBy_id(Long id, Pageable pageable);
}
