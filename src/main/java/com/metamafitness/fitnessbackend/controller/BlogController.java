package com.metamafitness.fitnessbackend.controller;

import com.metamafitness.fitnessbackend.dto.BlogDto;
import com.metamafitness.fitnessbackend.dto.BlogPatchDto;
import com.metamafitness.fitnessbackend.exception.ResourceDeletionNotAllowedException;
import com.metamafitness.fitnessbackend.exception.ResourceOwnershipException;
import com.metamafitness.fitnessbackend.model.Blog;
import com.metamafitness.fitnessbackend.model.GenericEnum;
import com.metamafitness.fitnessbackend.model.User;
import com.metamafitness.fitnessbackend.repository.BlogRepository;
import com.metamafitness.fitnessbackend.service.BlogService;
import com.metamafitness.fitnessbackend.service.StorageService;
import com.metamafitness.fitnessbackend.service.UserService;
import com.metamafitness.fitnessbackend.validator.ValidPicture;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.metamafitness.fitnessbackend.common.CoreConstant.Exception.AUTHORIZATION_RESOURCE_DELETION_NOT_ALLOWED;
import static com.metamafitness.fitnessbackend.common.CoreConstant.Exception.AUTHORIZATION_RESOURCE_OWNERSHIP;
import static com.metamafitness.fitnessbackend.common.CoreConstant.Pagination.DEFAULT_PAGE_NUMBER;
import static com.metamafitness.fitnessbackend.common.CoreConstant.Pagination.DEFAULT_PAGE_SIZE;

@RestController
@RequestMapping("/api/blogs")
@Validated
public class BlogController extends GenericController<Blog, BlogDto> {
    private final BlogService blogService;
    private final UserService userService;
    private final BlogRepository blogRepository;
    private final StorageService storageService;

    public BlogController(BlogService blogService, UserService userService, BlogRepository blogRepository, StorageService storageService) {
        this.blogService = blogService;
        this.userService = userService;
        this.blogRepository = blogRepository;
        this.storageService = storageService;
    }

    @Override
    public Blog convertToEntity(BlogDto dto) {
        final User currentUser = getCurrentUser();
        final Blog entity = super.convertToEntity(dto);
        entity.setCreatedBy(currentUser);
        return entity;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BlogDto> update(@PathVariable("id") Long id, @RequestBody BlogPatchDto blogDto) {
        final Blog blog = blogService.findById(id);
        if (isNotOwner(blog)) {
            throw new ResourceOwnershipException(new ResourceOwnershipException(), AUTHORIZATION_RESOURCE_OWNERSHIP, null);
        }
        ModelMapper modelMapper = getModelMapper();

        // Save the original skipNullEnabled value
        boolean originalSkipNullEnabled = modelMapper.getConfiguration().isSkipNullEnabled();

        // Set skipNullEnabled to true for this mapping operation
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(blogDto, blog);

        // Set skipNullEnabled back to its original value
        modelMapper.getConfiguration().setSkipNullEnabled(originalSkipNullEnabled);

        Blog updatedBlog = blogService.patch(blog);

        return ResponseEntity.status(HttpStatus.OK).body(convertToDto(updatedBlog));

    }

    private boolean isNotOwner(Blog blogFound) {
        final Long currentUserId = getCurrentUserId();
        final Long resourceOwnerId = blogFound.getCreatedBy().getId();
        return !currentUserId.equals(resourceOwnerId);
    }

    @GetMapping("/me")
    public ResponseEntity<List<BlogDto>> findTrainerBlogs(@RequestParam(value = "page", defaultValue = "" + DEFAULT_PAGE_NUMBER) Integer page,
                                                          @RequestParam(value = "size", defaultValue = "" + DEFAULT_PAGE_SIZE) Integer size) {
        Long currentUserId = getCurrentUserId();

        List<Blog> blogs = blogService.findByCreator(currentUserId, page, size);

        List<BlogDto> dto = blogs.stream().map(this::convertToDto).collect(Collectors.toList());
        long totalPrograms = blogService.countByCreator(currentUserId);
        int totalPages = (int) Math.ceil((double) totalPrograms / size);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Pages", String.valueOf(totalPages));
        headers.add("Access-Control-Expose-Headers", "X-Total-Pages");

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(dto);
    }

    @GetMapping("/trainers/{trainerId}")
    public ResponseEntity<List<BlogDto>> findTrainerBlogs(@PathVariable("trainerId") Long id,
                                                          @RequestParam(value = "page", defaultValue = "" + DEFAULT_PAGE_NUMBER) Integer page,
                                                          @RequestParam(value = "size", defaultValue = "" + DEFAULT_PAGE_SIZE) Integer size) {
        User userFound = userService.findById(id);

        List<Blog> blogs = blogService.findByCreator(userFound.getId(), page, size);

        List<BlogDto> dto = blogs.stream().map(this::convertToDto).collect(Collectors.toList());
        long totalPrograms = blogService.countByCreator(id);
        int totalPages = (int) Math.ceil((double) totalPrograms / size);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Pages", String.valueOf(totalPages));
        headers.add("Access-Control-Expose-Headers", "X-Total-Pages");

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(dto);
    }

    @PatchMapping("/{id}/submit")
    public ResponseEntity<BlogDto> submit(@PathVariable("id") Long id) {
        final Blog blog = blogService.findById(id);
        if (isNotOwner(blog)) {
            throw new ResourceOwnershipException(new ResourceOwnershipException(), AUTHORIZATION_RESOURCE_OWNERSHIP, null);
        }

        blog.setState(GenericEnum.BlogState.SUBMITTED);

        Blog submittedBlog = blogService.update(id, blog);

        return ResponseEntity.status(HttpStatus.OK).body(convertToDto(submittedBlog));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable("id") Long id) {
        final Blog blog = blogService.findById(id);
        if (isNotOwner(blog)) {
            throw new ResourceOwnershipException(new ResourceOwnershipException(), AUTHORIZATION_RESOURCE_OWNERSHIP, null);
        }
        if (!GenericEnum.BlogState.IN_PROGRESS.equals(blog.getState())) {
            throw new ResourceDeletionNotAllowedException(new ResourceDeletionNotAllowedException(), AUTHORIZATION_RESOURCE_DELETION_NOT_ALLOWED, null);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(blogService.delete(id));
    }

    @PostMapping
    public ResponseEntity<BlogDto> save(@RequestPart(value = "blog") @Valid BlogDto blogDto, @Valid @ValidPicture @RequestPart(value = "blog-picture") MultipartFile picture) {

        Blog blogEntity = convertToEntity(blogDto);
        String pictureUrl = storageService.save(picture);

        Blog savedBlog = createBlog(blogEntity, pictureUrl);

        BlogDto savedBlogDto = convertToDto(savedBlog);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBlogDto);
    }

    private Blog createBlog(Blog blogEntity, String pictureUrl) {
        blogEntity.setState(GenericEnum.BlogState.IN_PROGRESS);
        blogEntity.setPicture(pictureUrl);
        return blogService.save(blogEntity);
    }

}
