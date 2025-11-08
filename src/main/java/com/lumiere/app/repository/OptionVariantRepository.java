// src/main/java/com/lumi/app/repository/OptionVariantRepository.java
package com.lumiere.app.repository;

import com.lumiere.app.domain.OptionVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
@Repository
public interface OptionVariantRepository extends JpaRepository<OptionVariant, Long> {
    List<OptionVariant> findByProductVariant_Id(Long productVariantId);
    void deleteByProductVariant_IdAndOptionSelect_Id(Long variantId, Long selectId);
    boolean existsByProductVariant_IdAndOptionSelect_Id(Long variantId, Long selectId);
    List<OptionVariant> findByProductVariant_Product_Id(Long productId);
    List<OptionVariant> findByProductVariant_IdIn(Collection<Long> variantIds);
}
