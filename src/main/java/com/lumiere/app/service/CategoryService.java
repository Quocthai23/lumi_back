package com.lumiere.app.service;

import com.lumiere.app.service.dto.CategoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CategoryService {

    CategoryDTO save(CategoryDTO dto);            // create
    CategoryDTO update(CategoryDTO dto);          // full update (PUT)
    Optional<CategoryDTO> partialUpdate(CategoryDTO dto); // PATCH

    Page<CategoryDTO> findAll(Pageable pageable);
    Optional<CategoryDTO> findOne(Long id);
    void delete(Long id);
}
