package com.lumiere.app.repository;

import com.lumiere.app.domain.OptionGroup;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OptionGroupRepository extends JpaRepository<OptionGroup, Long> {
    @EntityGraph(attributePaths = "selects")
    List<OptionGroup> findByProduct_IdOrderByPositionAscIdAsc(Long productId);
    boolean existsByProduct_IdAndCodeIgnoreCase(Long productId, String code);
}
