// src/main/java/com/lumiere/app/web/rest/OptionGroupResource.java
package com.lumiere.app.web.rest;

import com.lumiere.app.service.OptionGroupService;
import com.lumiere.app.service.dto.OptionGroupDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/option-groups")
@RequiredArgsConstructor
public class OptionGroupResource {

  private final OptionGroupService service;

  @GetMapping
  public ResponseEntity<List<OptionGroupDTO>> findByProduct(@RequestParam Long productId){
    return ResponseEntity.ok(service.findByProduct(productId));
  }

  @PostMapping
  public ResponseEntity<OptionGroupDTO> create(@Valid @RequestBody OptionGroupDTO dto){
    OptionGroupDTO result = service.create(dto);
    return ResponseEntity.created(URI.create("/api/option-groups/" + result.getId())).body(result);
  }

  @PutMapping("/{id}")
  public ResponseEntity<OptionGroupDTO> update(@PathVariable Long id, @Valid @RequestBody OptionGroupDTO dto){
    return ResponseEntity.ok(service.update(id, dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id){
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
