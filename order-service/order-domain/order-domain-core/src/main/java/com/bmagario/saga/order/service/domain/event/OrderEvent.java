package com.bmagario.saga.order.service.domain.event;

import com.bmagario.saga.order.service.domain.entity.Order;
import java.time.ZonedDateTime;

public abstract class OrderEvent implements DomainEvent<Order> {
    private final Order order;
    private final ZonedDateTime createdAt;

    public OrderEvent(Order order, ZonedDateTime created) {
        this.order = order;
        this.createdAt = created;
    }

    public Order getOrder() {
        return order;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }
}
