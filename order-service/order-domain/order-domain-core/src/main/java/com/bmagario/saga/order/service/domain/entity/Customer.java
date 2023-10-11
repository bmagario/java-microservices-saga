package com.bmagario.saga.order.service.domain.entity;

import com.bmagario.saga.order.service.domain.valueobject.CustomerId;

public class Customer extends AggregateRoot<CustomerId> {

    public Customer() {
    }

    public Customer(CustomerId customerId) {
        super.setId(customerId);
    }
}
