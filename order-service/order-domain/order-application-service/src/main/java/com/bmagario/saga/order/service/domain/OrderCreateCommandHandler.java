package com.bmagario.saga.order.service.domain;

import com.bmagario.saga.order.service.domain.dto.create.CreateOrderCommand;
import com.bmagario.saga.order.service.domain.dto.create.CreateOrderResponse;
import com.bmagario.saga.order.service.domain.entity.Customer;
import com.bmagario.saga.order.service.domain.entity.Order;
import com.bmagario.saga.order.service.domain.entity.Restaurant;
import com.bmagario.saga.order.service.domain.event.OrderCreatedEvent;
import com.bmagario.saga.order.service.domain.exception.OrderDomainException;
import com.bmagario.saga.order.service.domain.mapper.OrderDataMapper;
import com.bmagario.saga.order.service.domain.ports.output.repository.CustomerRepository;
import com.bmagario.saga.order.service.domain.ports.output.repository.OrderRepository;
import com.bmagario.saga.order.service.domain.ports.output.repository.RestaurantRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class OrderCreateCommandHandler {

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderDataMapper orderDataMapper;

    public OrderCreateCommandHandler(OrderDomainService orderDomainService,
                                     OrderRepository orderRepository,
                                     CustomerRepository customerRepository,
                                     RestaurantRepository restaurantRepository,
                                     OrderDataMapper orderDataMapper) {
        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderDataMapper = orderDataMapper;
    }

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        checkCustomer(createOrderCommand.getCustomerId());
        Restaurant restaurant = checkRestaurant(createOrderCommand);
        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        OrderCreatedEvent orderCreatedEvent =
                orderDomainService.validateAndInitiateOrder(order, restaurant);
        Order orderResult = saveOrder(order);
        log.info("Order created with id {}", orderResult.getId().getValue());
        return orderDataMapper.orderToCreateOrderResponse(order);
    }

    private void checkCustomer(UUID customerId) {
        Optional<Customer> customer = customerRepository.findCustomer(customerId);

        customer.orElseThrow(() -> {
            log.warn("Could not find customer with id {} ", customerId);
            throw new OrderDomainException("Could not find customer with id " + customerId);
        });
    }

    private Restaurant checkRestaurant(CreateOrderCommand createOrderCommand) {
        Restaurant restaurant = orderDataMapper.createOrderCommandToRestaurant(createOrderCommand);
        return restaurantRepository
                .findRestaurantInformation(restaurant)
                .orElseThrow(() -> {
                    log.warn("Could not find restaurant with id {} ", restaurant.getId());
                    return new OrderDomainException(
                            "Could not find restaurant with id " + restaurant.getId());
                });

    }

    private Order saveOrder(Order order) {
        Order orderResult = orderRepository.save(order);
        if (orderResult == null) {
            log.error("Could not save order!");
            throw new OrderDomainException("Could not save order!");
        }
        log.info("Order saved with id: {}", orderResult.getId().getValue());
        return orderResult;
    }
}
