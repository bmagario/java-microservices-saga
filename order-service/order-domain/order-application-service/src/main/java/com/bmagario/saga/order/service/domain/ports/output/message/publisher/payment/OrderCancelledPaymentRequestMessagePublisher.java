package com.bmagario.saga.order.service.domain.ports.output.message.publisher.payment;

import com.bmagario.saga.order.service.domain.event.OrderCancelledEvent;
import com.bmagario.saga.order.service.domain.event.publisher.DomainEventPublisher;

public interface OrderCancelledPaymentRequestMessagePublisher
        extends DomainEventPublisher<OrderCancelledEvent> {
}
