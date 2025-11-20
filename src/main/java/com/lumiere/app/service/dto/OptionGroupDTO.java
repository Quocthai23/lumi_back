// src/main/java/com/lumiere/app/service/dto/OptionGroupDTO.java
package com.lumiere.app.service.dto;

import com.lumiere.app.domain.OptionSelect;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OptionGroupDTO {
  private Long id;
  private Long productId;
  private String name;
  private String code;
  private String variantDefaultType;
  private Integer position;
  private List<OptionSelectDTO> optionSelectDTOS;
  private Set<OptionSelect> selects = new LinkedHashSet<>();

}
