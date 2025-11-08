// src/main/java/com/lumiere/app/service/OptionGroupService.java
package com.lumiere.app.service;

import com.lumiere.app.service.dto.OptionGroupDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OptionGroupService {
  OptionGroupDTO create(OptionGroupDTO dto);
  OptionGroupDTO update(Long id, OptionGroupDTO dto);
  @Transactional(readOnly = true)
  List<OptionGroupDTO> findByProduct(Long productId);
  void delete(Long id);
}
