package com.metamafitness.fitnessbackend.service;

import com.metamafitness.fitnessbackend.model.Blog;

import java.util.List;

public interface BlogService extends GenericService<Blog> {
    List<Blog> findByCreator(Long id, int page, int size);
    long countByCreator(Long currentUserId);
}
