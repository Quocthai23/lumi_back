// src/main/java/com/lumiere/app/service/impl/OptionSelectServiceImpl.java
package com.lumiere.app.service.impl;

import com.lumiere.app.domain.OptionGroup;
import com.lumiere.app.domain.OptionSelect;
import com.lumiere.app.repository.OptionGroupRepository;
import com.lumiere.app.repository.OptionSelectRepository;
import com.lumiere.app.service.OptionSelectService;
import com.lumiere.app.service.dto.OptionSelectDTO;
import com.lumiere.app.service.mapper.OptionSelectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OptionSelectServiceImpl implements OptionSelectService {

  private final OptionSelectRepository repo;
  private final OptionGroupRepository optionGroupRepo;
  private final OptionSelectMapper mapper;

  @Override
  public OptionSelectDTO create(OptionSelectDTO dto){
    if (repo.existsByOptionGroup_IdAndCodeIgnoreCase(dto.getOptionGroupId(), dto.getCode()))
      throw new IllegalArgumentException("OptionSelect code duplicated in group");
    OptionSelect e = mapper.toEntity(dto);
    // Load OptionGroup từ repository để đảm bảo entity tồn tại
    OptionGroup group = optionGroupRepo.findById(dto.getOptionGroupId())
        .orElseThrow(() -> new EntityNotFoundException("OptionGroup not found with id: " + dto.getOptionGroupId()));
    e.setOptionGroup(group);
    return mapper.toDto(repo.save(e));
  }

  @Override
  public OptionSelectDTO update(Long id, OptionSelectDTO dto){
    OptionSelect e = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("OptionSelect not found"));
    if (!e.getCode().equalsIgnoreCase(dto.getCode())
        && repo.existsByOptionGroup_IdAndCodeIgnoreCase(e.getOptionGroup().getId(), dto.getCode())) {
      throw new IllegalArgumentException("OptionSelect code duplicated in group");
    }
    e.setName(dto.getName());
    e.setCode(dto.getCode());
    e.setPosition(dto.getPosition());
    e.setActive(dto.getActive() != null ? dto.getActive() : Boolean.TRUE);
    return mapper.toDto(repo.save(e));
  }

  @Override
  @Transactional(readOnly = true)
  public List<OptionSelectDTO> findByGroup(Long groupId){
    return repo.findByOptionGroup_IdOrderByPositionAscIdAsc(groupId).stream().map(mapper::toDto).toList();
  }

  @Override
  public void delete(Long id){
    repo.deleteById(id);
  }
  @Override
  public List<OptionSelectDTO> bulkCreate(List<OptionSelectDTO> items) {
        return items.stream().map(this::create).toList();
    }


}
