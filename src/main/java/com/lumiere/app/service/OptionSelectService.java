// src/main/java/com/lumiere/app/service/OptionSelectService.java
package com.lumiere.app.service;

import com.lumiere.app.service.dto.OptionSelectDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OptionSelectService {
  OptionSelectDTO create(OptionSelectDTO dto);
  OptionSelectDTO update(Long id, OptionSelectDTO dto);
  @Transactional(readOnly = true)
  List<OptionSelectDTO> findByGroup(Long groupId);
  void delete(Long id);

    List<OptionSelectDTO> bulkCreate(List<OptionSelectDTO> items);
}
