// src/main/java/com/lumiere/app/service/impl/OptionVariantServiceImpl.java
package com.lumiere.app.service.impl;

import com.lumiere.app.domain.OptionVariant;
import com.lumiere.app.domain.Product;
import com.lumiere.app.domain.ProductVariant;
import com.lumiere.app.repository.OptionSelectRepository;
import com.lumiere.app.repository.OptionVariantRepository;
import com.lumiere.app.repository.ProductRepository;
import com.lumiere.app.repository.ProductVariantRepository;
import com.lumiere.app.service.OptionVariantService;
import com.lumiere.app.service.dto.GroupSelectReq;
import com.lumiere.app.service.dto.OptionVariantDTO;
import com.lumiere.app.service.dto.SyncMixResult;
import com.lumiere.app.service.mapper.OptionVariantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class OptionVariantServiceImpl implements OptionVariantService {

    private final OptionVariantRepository repo;
    private final OptionVariantMapper mapper;
    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final OptionSelectRepository optionSelectRepository;
  @Override
  public List<OptionVariantDTO> assign(Long variantId, List<Long> optionSelectIds){
    List<OptionVariantDTO> created = new ArrayList<>();
    for (Long sid : optionSelectIds) {
      if (repo.existsByProductVariant_IdAndOptionSelect_Id(variantId, sid)) continue;
      OptionVariantDTO dto = OptionVariantDTO.builder()
        .productVariantId(variantId)
        .optionSelectId(sid)
        .build();
      OptionVariant saved = repo.save(mapper.toEntity(dto));
      created.add(mapper.toDto(saved));
    }
    return created;
  }

  @Override
  public void unassign(Long variantId, Long selectId){
    repo.deleteByProductVariant_IdAndOptionSelect_Id(variantId, selectId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<OptionVariantDTO> findByVariant(Long variantId){
    return repo.findByProductVariant_Id(variantId).stream().map(mapper::toDto).toList();
  }

  @Override
  public List<OptionVariantDTO> replaceAll(Long variantId, List<Long> newSelectIds){
    repo.findByProductVariant_Id(variantId).forEach(e -> repo.deleteById(e.getId()));
    return assign(variantId, newSelectIds);
  }

    /**
     * Đồng bộ mix biến thể theo danh sách group & selectIds.
     * - Tạo mới các biến thể cho mix mới
     * - Xoá các biến thể không còn trong input
     * - Giữ nguyên các biến thể trùng mix
     */
    @Override
    public SyncMixResult syncVariantMixes(Long productId, List<GroupSelectReq> groups) {
        // 0) Chuẩn hoá input: bỏ group rỗng
        List<GroupSelectReq> normalized = groups.stream()
            .filter(g -> g.getSelectIds() != null && !g.getSelectIds().isEmpty())
            .toList();
        if (normalized.isEmpty()) {
            // Nếu không có group nào ⇒ xoá hết biến thể thuộc productId
            List<ProductVariant> exists = productVariantRepository.findAllByProduct_Id(productId);
            List<Long> delIds = exists.stream().map(ProductVariant::getId).toList();
            if (!delIds.isEmpty()) productVariantRepository.deleteByIdIn(delIds);
            return SyncMixResult.builder()
                .createdVariantIds(List.of())
                .deletedVariantIds(delIds)
                .keptVariantIds(List.of())
                .build();
        }

        // 1) Sinh tất cả mix mới (cartesian product)
        List<List<Long>> newMixes = cartesian(normalized.stream()
            .map(GroupSelectReq::getSelectIds)
            .toList());

        // Canonical key: sort asc rồi join bằng '-'
        // (đảm bảo tính duy nhất bất kể thứ tự group)
        Map<String, List<Long>> newKeyToMix = newMixes.stream().collect(
            java.util.stream.Collectors.toMap(
                this::keyOf,
                mix -> mix,
                (a, b) -> a,
                java.util.LinkedHashMap::new
            )
        );
        Set<String> newKeys = newKeyToMix.keySet();

        // 2) Chuẩn bị map existing: variantId -> key
        List<ProductVariant> existingVariants = productVariantRepository.findAllByProduct_Id(productId);
        List<Long> existingVariantIds = existingVariants.stream().map(ProductVariant::getId).toList();
        List<OptionVariant> existingOV = existingVariantIds.isEmpty()
            ? List.of()
            : repo.findByProductVariant_IdIn(existingVariantIds);

        // Gom các selectId theo mỗi variant
        Map<Long, Set<Long>> variantToSelects = new java.util.HashMap<>();
        for (OptionVariant ov : existingOV) {
            variantToSelects.computeIfAbsent(ov.getProductVariant().getId(), k -> new java.util.HashSet<>())
                .add(ov.getOptionSelect().getId());
        }

        // Tính key cho từng variant hiện có
        Map<String, Long> existingKeyToVariantId = new java.util.HashMap<>();
        for (ProductVariant pv : existingVariants) {
            Set<Long> sids = variantToSelects.getOrDefault(pv.getId(), java.util.Set.of());
            if (sids.isEmpty()) continue; // bỏ qua biến thể trống
            String key = keyOf(new java.util.ArrayList<>(sids));
            existingKeyToVariantId.put(key, pv.getId());
        }
        Set<String> existingKeys = existingKeyToVariantId.keySet();

        // 3) Tính toCreate / toDelete / kept
        Set<String> toCreateKeys = new java.util.HashSet<>(newKeys);
        toCreateKeys.removeAll(existingKeys);

        Set<String> toDeleteKeys = new java.util.HashSet<>(existingKeys);
        toDeleteKeys.removeAll(newKeys);

        Set<String> keptKeys = new java.util.HashSet<>(newKeys);
        keptKeys.retainAll(existingKeys);

        List<Long> createdIds = new java.util.ArrayList<>();
        List<Long> deletedIds = new java.util.ArrayList<>();
        List<Long> keptIds = keptKeys.stream().map(existingKeyToVariantId::get).toList();

        // 4) Xoá biến thể thừa
        if (!toDeleteKeys.isEmpty()) {
            List<Long> del = toDeleteKeys.stream().map(existingKeyToVariantId::get).toList();
            if (!del.isEmpty()) {
                productVariantRepository.deleteByIdIn(del);
                deletedIds.addAll(del);
            }
        }

        // 5) Tạo mới biến thể cho các key cần create
        if (!toCreateKeys.isEmpty()) {
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

            for (String k : toCreateKeys) {
                List<Long> selectIds = newKeyToMix.get(k);
                // Tạo ProductVariant “trắng” (tuỳ bạn fill các field khác)
                ProductVariant pv = new ProductVariant();
                pv.setProduct(product);
                pv.setName(buildVariantName(product.getName(), selectIds)); // có thể thay bằng mapper/logic khác
                pv.setSku(generateSku(product, selectIds));                  // tuỳ rule của bạn
                pv.setIsDefault(Boolean.FALSE);
                pv.setPrice(java.math.BigDecimal.ZERO);                      // hoặc null nếu cho phép
                pv = productVariantRepository.save(pv);

                // Gắn OptionVariant
                for (Long sid : selectIds) {
                    OptionVariant ov = new OptionVariant();
                    ov.setProductVariant(pv);
                    ov.setOptionSelect(
                        optionSelectRepository.getReferenceById(sid)
                    );
                    repo.save(ov);
                }
                createdIds.add(pv.getId());
            }
        }

        return SyncMixResult.builder()
            .createdVariantIds(createdIds)
            .deletedVariantIds(deletedIds)
            .keptVariantIds(keptIds)
            .build();
    }

    // ==== Helpers ====

    private List<List<Long>> cartesian(List<List<Long>> lists) {
        List<List<Long>> result = new ArrayList<>();
        result.add(new ArrayList<>());
        for (List<Long> list : lists) {
            List<List<Long>> next = new ArrayList<>();
            for (List<Long> prefix : result) {
                for (Long val : list) {
                    List<Long> p = new ArrayList<>(prefix);
                    p.add(val);
                    next.add(p);
                }
            }
            result = next;
        }
        return result;
    }

    private String keyOf(List<Long> selectIds) {
        // sort & join
        java.util.List<Long> sorted = new java.util.ArrayList<>(selectIds);
        java.util.Collections.sort(sorted);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sorted.size(); i++) {
            if (i > 0) sb.append("-");
            sb.append(sorted.get(i));
        }
        return sb.toString();
    }

    private String buildVariantName(String productName, List<Long> selectIds) {
        // Bạn có thể map id -> tên để hiển thị “Màu/Size…”
        // Ở đây demo: nối id cho nhanh
        return (productName == null ? "" : productName + " - ") + keyOf(selectIds);
    }

    private String generateSku(Product product, List<Long> selectIds) {
        // Demo rule: PRODCODE-<joinedSelectIds>-<timestamp6>
        String prefix = (product.getCode() != null ? product.getCode() : "P" + product.getId());
        String joined = keyOf(selectIds).replace("-", ".");
        String suffix = String.valueOf(System.currentTimeMillis()).substring(7); // tạm
        return prefix + "-" + joined + "-" + suffix;
    }

}
