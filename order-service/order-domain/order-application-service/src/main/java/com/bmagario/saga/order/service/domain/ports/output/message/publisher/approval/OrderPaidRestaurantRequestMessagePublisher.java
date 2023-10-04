package com.bmagario.saga.order.service.domain.ports.output.message.publisher.approval;

import com.bmagario.saga.order.service.domain.event.OrderPaidEvent;
import com.bmagario.saga.order.service.domain.event.publisher.DomainEventPublisher;

public interface OrderPaidRestaurantRequestMessagePublisher
        extends DomainEventPublisher<OrderPaidEvent> {
}
