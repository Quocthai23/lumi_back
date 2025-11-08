// src/main/java/com/lumiere/app/web/rest/OptionVariantResource.java
package com.lumiere.app.web.rest;

import com.lumiere.app.service.OptionVariantService;
import com.lumiere.app.service.dto.GroupSelectReq;
import com.lumiere.app.service.dto.OptionVariantDTO;
import com.lumiere.app.service.dto.SyncMixResult;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/option-variants")
@RequiredArgsConstructor
public class OptionVariantResource {

  private final OptionVariantService service;

  @GetMapping("/by-variant/{variantId}")
  public ResponseEntity<List<OptionVariantDTO>> findByVariant(@PathVariable Long variantId){
    return ResponseEntity.ok(service.findByVariant(variantId));
  }

  @PostMapping("/assign")
  public ResponseEntity<List<OptionVariantDTO>> assign(@RequestBody AssignReq req){
    return ResponseEntity.ok(service.assign(req.getVariantId(), req.getSelectIds()));
  }

  @PostMapping("/replace")
  public ResponseEntity<List<OptionVariantDTO>> replace(@RequestBody AssignReq req){
    return ResponseEntity.ok(service.replaceAll(req.getVariantId(), req.getSelectIds()));
  }

  @DeleteMapping("/unassign")
  public ResponseEntity<Void> unassign(@RequestParam Long variantId, @RequestParam Long selectId){
    service.unassign(variantId, selectId);
    return ResponseEntity.noContent().build();
  }

  @Data
  public static class AssignReq {
    @NotNull private Long variantId;
    @NotNull private List<Long> selectIds;
  }

    @PostMapping("/products/{productId}/variants/sync-by-groups")
    public ResponseEntity<SyncMixResult> syncByGroups(
        @PathVariable Long productId,
        @RequestBody List<GroupSelectReq> groups
    ){
        SyncMixResult res = service.syncVariantMixes(productId, groups);
        return ResponseEntity.ok(res);
    }

}
