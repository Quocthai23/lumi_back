// src/main/java/com/lumiere/app/service/dto/OptionSelectDTO.java
package com.lumiere.app.service.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OptionSelectDTO {
  private Long id;
  private Long optionGroupId;
  private String name;
  private String code;
  private Integer position;
  private Boolean active;
  private String optionGroupName;  // nếu cần show tên
}
