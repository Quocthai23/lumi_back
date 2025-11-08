package com.lumiere.app.repository;

import com.lumiere.app.domain.OptionGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionGroupRepository extends JpaRepository<OptionGroup, Long> {
    List<OptionGroup> findByProduct_IdOrderByPositionAscIdAsc(Long productId);
    boolean existsByProduct_IdAndCodeIgnoreCase(Long productId, String code);
}
