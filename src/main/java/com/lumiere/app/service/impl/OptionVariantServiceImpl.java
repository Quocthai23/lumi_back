// src/main/java/com/lumiere/app/service/impl/OptionVariantServiceImpl.java
package com.lumiere.app.service.impl;

import com.lumiere.app.domain.OptionSelect;
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
import com.lumiere.app.service.dto.ProductVariantDTO;
import com.lumiere.app.service.dto.SyncMixResult;
import com.lumiere.app.service.mapper.OptionVariantMapper;
import com.lumiere.app.service.mapper.ProductVariantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OptionVariantServiceImpl implements OptionVariantService {

    private final OptionVariantRepository repo;
    private final OptionVariantMapper mapper;
    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final OptionSelectRepository optionSelectRepository;
    private final ProductVariantMapper productVariantMapper;

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
    @Transactional
    public SyncMixResult syncVariantMixes(Long productId, List<GroupSelectReq> groups) {

        // (0) Chuẩn hoá input – bỏ group trống
        List<GroupSelectReq> normalized = groups.stream()
            .filter(g -> g.getSelectIds() != null && !g.getSelectIds().isEmpty())
            .toList();

        if (normalized.isEmpty()) {
            // Nếu hoàn toàn không có group nào → xoá hết variant khác
            List<ProductVariant> exists = productVariantRepository.findByProductId(productId);
            List<Long> delIds = exists.stream().map(ProductVariant::getId).toList();
            if (!delIds.isEmpty()) productVariantRepository.deleteByIdIn(delIds);

            return SyncMixResult.builder()
                .createdVariantIds(List.of())
                .deletedVariantIds(delIds)
                .keptVariantIds(List.of())
                .build();
        }

        // (1) Sinh cartesian mixes
        List<List<Long>> newMixes = cartesian(
            normalized.stream().map(GroupSelectReq::getSelectIds).toList()
        );

        // Key canonical
        Map<String, List<Long>> newKeyToMix = newMixes.stream().collect(
            Collectors.toMap(
                this::keyOf,
                mix -> mix,
                (a, b) -> a,
                LinkedHashMap::new
            )
        );
        Set<String> newKeys = newKeyToMix.keySet();

        // (2) Load biến thể hiện có
        List<ProductVariant> existingVariants = productVariantRepository.findByProductId(productId);
        List<Long> existingIds = existingVariants.stream().map(ProductVariant::getId).toList();

        List<OptionVariant> existingOV = existingIds.isEmpty()
            ? List.of()
            : repo.findByProductVariant_IdIn(existingIds);

        // Gom selectId theo variantId
        Map<Long, Set<Long>> variantToSelects = new HashMap<>();
        for (OptionVariant ov : existingOV) {
            variantToSelects.computeIfAbsent(ov.getProductVariant().getId(), k -> new HashSet<>())
                .add(ov.getOptionSelect().getId());
        }

        Map<String, Long> existingKeyToVariant = new HashMap<>();
        for (ProductVariant pv : existingVariants) {
            Set<Long> sids = variantToSelects.getOrDefault(pv.getId(), Set.of());
            if (!sids.isEmpty()) {
                String key = keyOf(new ArrayList<>(sids));
                existingKeyToVariant.put(key, pv.getId());
            }
        }

        // (3) Tính toCreate / toDelete / kept
        Set<String> toCreateKeys = new HashSet<>(newKeys);
        toCreateKeys.removeAll(existingKeyToVariant.keySet());

        Set<String> toDeleteKeys = new HashSet<>(existingKeyToVariant.keySet());
        toDeleteKeys.removeAll(newKeys);

        Set<String> keptKeys = new HashSet<>(newKeys);
        keptKeys.retainAll(existingKeyToVariant.keySet());

        List<Long> createdIds = new ArrayList<>();
        List<Long> deletedIds = new ArrayList<>();
        List<Long> keptIds = keptKeys.stream()
            .map(existingKeyToVariant::get)
            .toList();

        // (4) Xoá variant thừa
        if (!toDeleteKeys.isEmpty()) {
            List<Long> del = toDeleteKeys.stream().map(existingKeyToVariant::get).toList();
            if (!del.isEmpty()) {
                productVariantRepository.deleteByIdIn(del);
                deletedIds.addAll(del);
            }
        }

        // (5) Tạo mới variant nếu có
        if (!toCreateKeys.isEmpty()) {

            // Load product
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

            // Lấy toàn bộ OptionSelect cần dùng
            Set<Long> allSelectIds = new HashSet<>();
            for (String k : toCreateKeys) allSelectIds.addAll(newKeyToMix.get(k));

            List<OptionSelect> allSelects = optionSelectRepository.findAllByIdIn(allSelectIds);
            Map<Long, OptionSelect> selectMap = allSelects.stream()
                .collect(Collectors.toMap(OptionSelect::getId, s -> s));

            // Tạo ProductVariant (chưa flush)
            List<ProductVariant> newVariants = new ArrayList<>();
            List<String> orderedKeys = new ArrayList<>(toCreateKeys);

            for (String k : orderedKeys) {
                List<Long> sids = newKeyToMix.get(k);

                ProductVariant pv = new ProductVariant();
                pv.setProduct(product);
                pv.setName(buildVariantName(product.getName(), sids));
                pv.setSku(generateSku(product, sids));
                pv.setIsDefault(false);
                pv.setPrice(BigDecimal.ZERO);
                pv.setStockQuantity(0L);
                pv.setUrlImage("");

                newVariants.add(pv);
            }

            // Lưu tất cả ProductVariant
            List<ProductVariant> savedVariants = productVariantRepository.saveAll(newVariants);

            // Prepare OptionVariant
            List<OptionVariant> newOV = new ArrayList<>();

            for (int i = 0; i < orderedKeys.size(); i++) {
                String key = orderedKeys.get(i);
                ProductVariant pv = savedVariants.get(i);

                createdIds.add(pv.getId());

                for (Long sid : newKeyToMix.get(key)) {
                    OptionSelect s = selectMap.get(sid);
                    OptionVariant ov = new OptionVariant();
                    ov.setProductVariant(pv);
                    ov.setOptionSelect(s);
                    newOV.add(ov);
                }
            }

            // Lưu tất cả OptionVariant – Không flush sớm
            if (!newOV.isEmpty()) repo.saveAll(newOV);
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

    @Transactional(readOnly = true)
    @Override
    public ProductVariantDTO findVariantBySelectOptionIds(List<Long> selectOptions) {
        if (selectOptions == null || selectOptions.isEmpty()) {
            return null;
        }

        // Chuẩn hóa: bỏ trùng
        Set<Long> targetSet = new HashSet<>(selectOptions);

        // 1) Lấy tất cả OptionVariant có selectId thuộc targetSet
        List<OptionVariant> candidateMappings =
            repo.findByOptionSelect_IdIn(targetSet);

        if (candidateMappings.isEmpty()) {
            return null;
        }

        // 2) Lấy danh sách variantId ứng viên
        Set<Long> candidateVariantIds = candidateMappings.stream()
            .map(ov -> ov.getProductVariant().getId())
            .collect(Collectors.toSet());

        if (candidateVariantIds.isEmpty()) {
            return null;
        }

        // 3) Lấy TẤT CẢ mapping của các variant ứng viên
        List<OptionVariant> allMappingsForCandidates =
            repo.findByProductVariant_idIn(candidateVariantIds);

        // 4) Gom thành: variantId -> set<selectId>
        Map<Long, Set<Long>> variantToSelectIds = allMappingsForCandidates.stream()
            .collect(Collectors.groupingBy(
                ov -> ov.getProductVariant().getId(),
                Collectors.mapping(
                    ov -> ov.getOptionSelect().getId(),
                    Collectors.toSet()
                )
            ));

        // 5) Tìm variant có selectIds == targetSet (exact match)
        Long matchedVariantId = variantToSelectIds.entrySet().stream()
            .filter(e -> {
                Set<Long> variantSet = e.getValue();
                return variantSet.size() == targetSet.size()
                    && variantSet.containsAll(targetSet);
            })
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(null);

        if (matchedVariantId == null) {
            return null;
        }

        ProductVariant variant = productVariantRepository.findById(matchedVariantId)
            .orElse(null);

        if (variant == null) {
            return null;
        }

        return productVariantMapper.toDto(variant);
    }

}
