package com.lumiere.app.repository;

import com.lumiere.app.domain.CustomerInfo;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerInfoRepository extends JpaRepository<CustomerInfo, Long>, JpaSpecificationExecutor<CustomerInfo> {
    /**
     * Tìm tất cả thông tin địa chỉ của khách hàng.
     */
    List<CustomerInfo> findByCustomerId(Long customerId);

    /**
     * Tìm địa chỉ mặc định của khách hàng.
     */
    CustomerInfo findByCustomerIdAndIsDefaultTrue(Long customerId);
}
