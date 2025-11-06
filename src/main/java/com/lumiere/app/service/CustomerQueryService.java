package com.lumiere.app.service;

import com.lumiere.app.domain.*; // for static metamodels
import com.lumiere.app.domain.Customer;
import com.lumiere.app.repository.CustomerRepository;
import com.lumiere.app.service.criteria.CustomerCriteria;
import com.lumiere.app.service.dto.CustomerDTO;
import com.lumiere.app.service.mapper.CustomerMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Customer} entities in the database.
 * The main input is a {@link CustomerCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CustomerDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CustomerQueryService extends QueryService<Customer> {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerQueryService.class);

    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    public CustomerQueryService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    /**
     * Return a {@link Page} of {@link CustomerDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CustomerDTO> findByCriteria(CustomerCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Customer> specification = createSpecification(criteria);
        return customerRepository.fetchBagRelationships(customerRepository.findAll(specification, page)).map(customerMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CustomerCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Customer> specification = createSpecification(criteria);
        return customerRepository.count(specification);
    }

    /**
     * Function to convert {@link CustomerCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Customer> createSpecification(CustomerCriteria criteria) {
        Specification<Customer> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Customer_.id),
                buildStringSpecification(criteria.getFirstName(), Customer_.firstName),
                buildStringSpecification(criteria.getLastName(), Customer_.lastName),
                buildStringSpecification(criteria.getPhone(), Customer_.phone),
                buildSpecification(criteria.getTier(), Customer_.tier),
                buildRangeSpecification(criteria.getLoyaltyPoints(), Customer_.loyaltyPoints),
                buildSpecification(criteria.getUserId(), root -> root.join(Customer_.user, JoinType.LEFT).get(User_.id)),
                buildSpecification(criteria.getOrdersId(), root -> root.join(Customer_.orders, JoinType.LEFT).get(Orders_.id)),
                buildSpecification(criteria.getWishlistId(), root -> root.join(Customer_.wishlists, JoinType.LEFT).get(Product_.id)),
                buildSpecification(criteria.getAddressesId(), root -> root.join(Customer_.addresses, JoinType.LEFT).get(Address_.id)),
                buildSpecification(criteria.getLoyaltyHistoryId(), root ->
                    root.join(Customer_.loyaltyHistories, JoinType.LEFT).get(LoyaltyTransaction_.id)
                ),
                buildSpecification(criteria.getNotificationsId(), root ->
                    root.join(Customer_.notifications, JoinType.LEFT).get(Notification_.id)
                )
            );
        }
        return specification;
    }
}
