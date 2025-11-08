// src/main/java/com/lumiere/app/service/OptionVariantService.java
package com.lumiere.app.service;

import com.lumiere.app.service.dto.GroupSelectReq;
import com.lumiere.app.service.dto.OptionVariantDTO;
import com.lumiere.app.service.dto.SyncMixResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OptionVariantService {
  List<OptionVariantDTO> assign(Long variantId, List<Long> optionSelectIds);
  void unassign(Long variantId, Long selectId);
  @Transactional(readOnly = true)
  List<OptionVariantDTO> findByVariant(Long variantId);
  List<OptionVariantDTO> replaceAll(Long variantId, List<Long> newSelectIds);

    SyncMixResult syncVariantMixes(Long productId, List<GroupSelectReq> groups);
}
