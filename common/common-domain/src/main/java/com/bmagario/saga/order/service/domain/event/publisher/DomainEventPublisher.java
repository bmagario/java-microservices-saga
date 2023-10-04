package com.bmagario.saga.order.service.domain.event.publisher;

import com.bmagario.saga.order.service.domain.event.DomainEvent;

public interface DomainEventPublisher<T extends DomainEvent> {
    void publish(T domainEvent);
}
