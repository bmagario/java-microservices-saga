package com.bmagario.saga.order.service.domain;

import com.bmagario.saga.order.service.domain.dto.track.TrackOrderQuery;
import com.bmagario.saga.order.service.domain.dto.track.TrackOrderResponse;
import com.bmagario.saga.order.service.domain.entity.Order;
import com.bmagario.saga.order.service.domain.exception.OrderNotFoundException;
import com.bmagario.saga.order.service.domain.mapper.OrderDataMapper;
import com.bmagario.saga.order.service.domain.ports.output.repository.OrderRepository;
import com.bmagario.saga.order.service.domain.valueobject.TrackingId;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class OrderTrackCommandHandler {

    private final OrderDataMapper orderDataMapper;
    private final OrderRepository orderRepository;

    public OrderTrackCommandHandler(OrderDataMapper orderDataMapper,
                                    OrderRepository orderRepository) {
        this.orderDataMapper = orderDataMapper;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public TrackOrderResponse trackOrder(TrackOrderQuery trackOrderQuery) {
        Optional<Order> orderResult = orderRepository.findByTrackingId(
                new TrackingId(trackOrderQuery.getOrderTrackingId()));
        return orderResult
                .map(orderDataMapper::orderToTrackOrderResponse)
                .orElseThrow(() -> {
                    log.error("Could not find order with tracking id: {}",
                            trackOrderQuery.getOrderTrackingId());
                    return new OrderNotFoundException("Could not find order with tracking id: " +
                            trackOrderQuery.getOrderTrackingId());
                });
    }
}
