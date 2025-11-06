package com.lumiere.app.repository;

import com.lumiere.app.domain.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class CustomerRepositoryWithBagRelationshipsImpl implements CustomerRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String CUSTOMERS_PARAMETER = "customers";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Customer> fetchBagRelationships(Optional<Customer> customer) {
        return customer.map(this::fetchWishlists);
    }

    @Override
    public Page<Customer> fetchBagRelationships(Page<Customer> customers) {
        return new PageImpl<>(fetchBagRelationships(customers.getContent()), customers.getPageable(), customers.getTotalElements());
    }

    @Override
    public List<Customer> fetchBagRelationships(List<Customer> customers) {
        return Optional.of(customers).map(this::fetchWishlists).orElse(Collections.emptyList());
    }

    Customer fetchWishlists(Customer result) {
        return entityManager
            .createQuery(
                "select customer from Customer customer left join fetch customer.wishlists where customer.id = :id",
                Customer.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Customer> fetchWishlists(List<Customer> customers) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, customers.size()).forEach(index -> order.put(customers.get(index).getId(), index));
        List<Customer> result = entityManager
            .createQuery(
                "select customer from Customer customer left join fetch customer.wishlists where customer in :customers",
                Customer.class
            )
            .setParameter(CUSTOMERS_PARAMETER, customers)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
