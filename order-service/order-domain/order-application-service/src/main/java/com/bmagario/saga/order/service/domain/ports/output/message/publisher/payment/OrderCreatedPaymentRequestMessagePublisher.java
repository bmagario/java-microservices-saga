package com.bmagario.saga.order.service.domain.ports.output.message.publisher.payment;

import com.bmagario.saga.order.service.domain.event.OrderCreatedEvent;
import com.bmagario.saga.order.service.domain.event.publisher.DomainEventPublisher;

public interface OrderCreatedPaymentRequestMessagePublisher
        extends DomainEventPublisher<OrderCreatedEvent> {
}
