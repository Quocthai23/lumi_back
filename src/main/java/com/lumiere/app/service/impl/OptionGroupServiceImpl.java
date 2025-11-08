// src/main/java/com/lumiere/app/service/impl/OptionGroupServiceImpl.java
package com.lumiere.app.service.impl;

import com.lumiere.app.domain.OptionGroup;
import com.lumiere.app.repository.OptionGroupRepository;
import com.lumiere.app.service.OptionGroupService;
import com.lumiere.app.service.dto.OptionGroupDTO;
import com.lumiere.app.service.mapper.OptionGroupMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OptionGroupServiceImpl implements OptionGroupService {

  private final OptionGroupRepository repo;
  private final OptionGroupMapper mapper;

  @Override
  public OptionGroupDTO create(OptionGroupDTO dto){
    if (repo.existsByProduct_IdAndCodeIgnoreCase(dto.getProductId(), dto.getCode()))
      throw new IllegalArgumentException("OptionGroup code duplicated in product");
    OptionGroup e = mapper.toEntity(dto);
    return mapper.toDto(repo.save(e));
  }

  @Override
  public OptionGroupDTO update(Long id, OptionGroupDTO dto){
    OptionGroup e = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("OptionGroup not found"));
    if (!e.getCode().equalsIgnoreCase(dto.getCode())
        && repo.existsByProduct_IdAndCodeIgnoreCase(e.getProduct().getId(), dto.getCode())) {
      throw new IllegalArgumentException("OptionGroup code duplicated in product");
    }
    e.setName(dto.getName());
    e.setCode(dto.getCode());
    e.setPosition(dto.getPosition());
    return mapper.toDto(repo.save(e));
  }

  @Override
  @Transactional(readOnly = true)
  public List<OptionGroupDTO> findByProduct(Long productId){
    return repo.findByProduct_IdOrderByPositionAscIdAsc(productId).stream().map(mapper::toDto).toList();
  }

  @Override
  public void delete(Long id){
    repo.deleteById(id);
  }
}
