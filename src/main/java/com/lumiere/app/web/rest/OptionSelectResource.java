// src/main/java/com/lumiere/app/web/rest/OptionSelectResource.java
package com.lumiere.app.web.rest;

import com.lumiere.app.service.OptionSelectService;
import com.lumiere.app.service.dto.OptionSelectDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/option-selects")
@RequiredArgsConstructor
public class OptionSelectResource {

  private final OptionSelectService service;

  @GetMapping
  public ResponseEntity<List<OptionSelectDTO>> findByGroup(@RequestParam Long optionGroupId){
    return ResponseEntity.ok(service.findByGroup(optionGroupId));
  }

  @PostMapping
  public ResponseEntity<OptionSelectDTO> create(@Valid @RequestBody OptionSelectDTO dto){
    OptionSelectDTO result = service.create(dto);
    return ResponseEntity.created(URI.create("/api/option-selects/" + result.getId())).body(result);
  }

  @PutMapping("/{id}")
  public ResponseEntity<OptionSelectDTO> update(@PathVariable Long id, @Valid @RequestBody OptionSelectDTO dto){
    return ResponseEntity.ok(service.update(id, dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id){
    service.delete(id);
    return ResponseEntity.noContent().build();
  }

@PostMapping("/bulk")
public ResponseEntity<List<OptionSelectDTO>> bulkCreate(@Valid @RequestBody List<OptionSelectDTO> items) {
    List<OptionSelectDTO> saved = service.bulkCreate(items);
    return ResponseEntity.ok(saved);
}


}
