package com.lumiere.app.repository;

import com.lumiere.app.domain.Customer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Customer entity.
 *
 * When extending this class, extend CustomerRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface CustomerRepository
    extends CustomerRepositoryWithBagRelationships, JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    default Optional<Customer> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<Customer> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<Customer> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select customer from Customer customer left join fetch customer.user",
        countQuery = "select count(customer) from Customer customer"
    )
    Page<Customer> findAllWithToOneRelationships(Pageable pageable);

    @Query("select customer from Customer customer left join fetch customer.user")
    List<Customer> findAllWithToOneRelationships();

    @Query("select customer from Customer customer left join fetch customer.user where customer.id =:id")
    Optional<Customer> findOneWithToOneRelationships(@Param("id") Long id);

    /**
     * Tìm Customer theo User ID.
     */
    @Query("select customer from Customer customer where customer.user.id = :userId")
    Optional<Customer> findByUserId(@Param("userId") Long userId);

    /**
     * Đếm số khách hàng mới theo tháng trong năm.
     */
    @Query("""
        SELECT FUNCTION('YEAR', u.createdDate) as year, FUNCTION('MONTH', u.createdDate) as month, COUNT(DISTINCT c.id) as count
        FROM Customer c
        JOIN c.user u
        WHERE FUNCTION('YEAR', u.createdDate) = :year
        GROUP BY FUNCTION('YEAR', u.createdDate), FUNCTION('MONTH', u.createdDate)
        ORDER BY month
        """)
    List<Object[]> getNewCustomersByMonth(@Param("year") int year);

    /**
     * Tìm tất cả khách hàng có sinh nhật vào ngày cụ thể (chỉ so sánh tháng và ngày, không tính năm).
     *
     * @param month tháng (1-12)
     * @param day ngày trong tháng (1-31)
     * @return danh sách khách hàng có sinh nhật
     */
    @Query("SELECT c FROM Customer c WHERE FUNCTION('MONTH', c.birthday) = :month AND FUNCTION('DAY', c.birthday) = :day AND c.birthday IS NOT NULL")
    List<Customer> findByBirthdayMonthAndDay(@Param("month") int month, @Param("day") int day);
}
