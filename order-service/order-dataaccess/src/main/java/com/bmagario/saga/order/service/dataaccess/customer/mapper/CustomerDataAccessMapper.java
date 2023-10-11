package com.bmagario.saga.order.service.dataaccess.customer.mapper;

import com.bmagario.saga.order.service.dataaccess.customer.entity.CustomerEntity;
import com.bmagario.saga.order.service.domain.entity.Customer;
import com.bmagario.saga.order.service.domain.valueobject.CustomerId;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataAccessMapper {

    public Customer customerEntityToCustomer(CustomerEntity customerEntity) {
        return new Customer(new CustomerId(customerEntity.getId()));
    }
}
