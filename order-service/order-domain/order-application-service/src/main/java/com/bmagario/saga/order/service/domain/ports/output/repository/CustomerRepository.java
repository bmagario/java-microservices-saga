package com.bmagario.saga.order.service.domain.ports.output.repository;

import com.bmagario.saga.order.service.domain.entity.Customer;
import java.util.Optional;

public interface CustomerRepository {
    Optional<Customer> findCustomer(Customer customer);
}
