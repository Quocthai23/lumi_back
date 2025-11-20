package com.lumiere.app.repository;

import com.lumiere.app.domain.CustomerInfo;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerInfoRepository extends JpaRepository<CustomerInfo, Long>, JpaSpecificationExecutor<CustomerInfo> {
}
