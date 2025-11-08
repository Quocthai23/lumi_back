// src/main/java/com/lumi/app/repository/InventoryAdjustmentRepository.java
package com.lumiere.app.repository;

import com.lumiere.app.domain.InventoryAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryAdjustmentRepository extends JpaRepository<InventoryAdjustment, Long> { }
