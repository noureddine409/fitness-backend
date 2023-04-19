package com.metamafitness.fitnessbackend.service.impl;

import com.metamafitness.fitnessbackend.model.Blog;
import com.metamafitness.fitnessbackend.repository.BlogRepository;
import com.metamafitness.fitnessbackend.service.BlogService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogServiceImpl extends GenericServiceImpl<Blog> implements BlogService {
    private final BlogRepository blogRepository;
    public BlogServiceImpl(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    @Override
    public List<Blog> findByCreator(Long id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return blogRepository.findByCreatedBy_id(id, pageable);
    }
    @Override
    public long countByCreator(Long currentUserId) {
        return blogRepository.countByCreatedBy_id(currentUserId);
    }
}
