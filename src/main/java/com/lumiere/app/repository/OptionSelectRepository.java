package com.lumiere.app.repository;

import com.lumiere.app.domain.OptionSelect;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface OptionSelectRepository extends JpaRepository<OptionSelect, Long> {
    List<OptionSelect> findByOptionGroup_IdOrderByPositionAscIdAsc(Long optionGroupId);
    boolean existsByOptionGroup_IdAndCodeIgnoreCase(Long groupId, String code);
    List<OptionSelect> findAllByIdIn(Collection<Long> ids);

}
