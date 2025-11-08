package com.lumiere.app.service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SyncMixResult {
  private List<Long> createdVariantIds;
  private List<Long> deletedVariantIds;
  private List<Long> keptVariantIds;
}
