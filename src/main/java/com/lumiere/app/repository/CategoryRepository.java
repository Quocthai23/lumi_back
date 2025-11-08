package com.lumiere.app.repository;

import com.lumiere.app.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByFatherId(Long fatherId);
    boolean existsByName(String name);
}
