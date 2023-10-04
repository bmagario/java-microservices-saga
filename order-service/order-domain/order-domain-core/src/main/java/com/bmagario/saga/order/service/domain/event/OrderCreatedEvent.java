package com.bmagario.saga.order.service.domain.event;

import com.bmagario.saga.order.service.domain.entity.Order;
import java.time.ZonedDateTime;

public class OrderCreatedEvent extends OrderEvent {
    public OrderCreatedEvent(Order order, ZonedDateTime created) {
        super(order, created);
    }
}
