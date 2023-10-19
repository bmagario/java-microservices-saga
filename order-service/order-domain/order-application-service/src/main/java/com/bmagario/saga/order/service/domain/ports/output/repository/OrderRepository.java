package com.bmagario.saga.order.service.domain.ports.output.repository;

import com.bmagario.saga.order.service.domain.entity.Order;
import com.bmagario.saga.order.service.domain.valueobject.OrderId;
import com.bmagario.saga.order.service.domain.valueobject.TrackingId;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);

    Optional<Order> findByTrackingId(TrackingId trackingId);

    Optional<Order> findById(OrderId orderId);
}
