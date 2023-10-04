package com.bmagario.saga.order.service.domain.event;

import com.bmagario.saga.order.service.domain.entity.Order;
import java.time.ZonedDateTime;

public class OrderCancelledEvent extends OrderEvent {
    public OrderCancelledEvent(Order order, ZonedDateTime created) {
        super(order, created);
    }
}
