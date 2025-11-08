package com.lumiere.app.service.impl;

import com.lumiere.app.domain.Category;
import com.lumiere.app.repository.CategoryRepository;
import com.lumiere.app.service.CategoryService;
import com.lumiere.app.service.dto.CategoryDTO;
import com.lumiere.app.service.mapper.CategoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public CategoryDTO save(CategoryDTO dto) {
        log.debug("Request to save Category : {}", dto);
        Category entity = categoryMapper.toEntity(dto);
        entity = categoryRepository.save(entity);
        return categoryMapper.toDto(entity);
    }

    @Override
    public CategoryDTO update(CategoryDTO dto) {
        log.debug("Request to update Category : {}", dto);
        Category entity = categoryMapper.toEntity(dto);
        entity = categoryRepository.save(entity);
        return categoryMapper.toDto(entity);
    }

    @Override
    public Optional<CategoryDTO> partialUpdate(CategoryDTO dto) {
        log.debug("Request to partially update Category : {}", dto);
        return categoryRepository
            .findById(dto.getId())
            .map(existing -> {
                categoryMapper.partialUpdate(existing, dto);
                return existing;
            })
            .map(categoryRepository::save)
            .map(categoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Categories");
        return categoryRepository.findAll(pageable).map(categoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryDTO> findOne(Long id) {
        log.debug("Request to get Category : {}", id);
        return categoryRepository.findById(id).map(categoryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Category : {}", id);
        categoryRepository.deleteById(id);
    }
}
