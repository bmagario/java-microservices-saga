package com.bmagario.saga.order.service.domain;

import com.bmagario.saga.order.service.domain.entity.Order;
import com.bmagario.saga.order.service.domain.exception.OrderNotFoundException;
import com.bmagario.saga.order.service.domain.ports.output.repository.OrderRepository;
import com.bmagario.saga.order.service.domain.valueobject.OrderId;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderSagaHelper {
    private final OrderRepository orderRepository;

    public OrderSagaHelper(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order findOrder(String orderId) {
        Optional<Order> orderResponse =
                orderRepository.findById(new OrderId(UUID.fromString(orderId)));
        return orderResponse.orElseThrow(() -> {
            log.error("Order with id: {} could not be found!", orderId);
            throw new OrderNotFoundException("Order with id: " + orderId + " could not be found!");
        });
    }

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }
}
