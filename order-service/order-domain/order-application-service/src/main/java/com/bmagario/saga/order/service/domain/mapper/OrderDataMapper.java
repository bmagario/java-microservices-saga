package com.bmagario.saga.order.service.domain.mapper;

import com.bmagario.saga.order.service.domain.dto.create.CreateOrderCommand;
import com.bmagario.saga.order.service.domain.dto.create.CreateOrderResponse;
import com.bmagario.saga.order.service.domain.dto.create.OrderAddress;
import com.bmagario.saga.order.service.domain.entity.Order;
import com.bmagario.saga.order.service.domain.entity.OrderItem;
import com.bmagario.saga.order.service.domain.entity.Product;
import com.bmagario.saga.order.service.domain.entity.Restaurant;
import com.bmagario.saga.order.service.domain.valueobject.CustomertId;
import com.bmagario.saga.order.service.domain.valueobject.Money;
import com.bmagario.saga.order.service.domain.valueobject.ProductId;
import com.bmagario.saga.order.service.domain.valueobject.RestaurantId;
import com.bmagario.saga.order.service.domain.valueobject.StreetAddress;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class OrderDataMapper {
    public Restaurant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand) {
        return Restaurant.builder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(createOrderCommand.getItems().stream()
                        .map(orderItem -> new Product(new ProductId(orderItem.getProductId())))
                        .collect(Collectors.toList()))
                .build();

    }

    public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
        return Order.builder()
                .customertId(new CustomertId(createOrderCommand.getCustomerId()))
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .deliveryAddress(
                        orderAddressToStreetAddress(createOrderCommand.getAddress()))
                .price(new Money(createOrderCommand.getPrice()))
                .items(orderItemsToOrderItemsEntities(createOrderCommand.getItems()))
                .build();
    }

    public CreateOrderResponse orderToCreateOrderResponse(Order order) {
        return CreateOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .build();
    }

    private List<OrderItem> orderItemsToOrderItemsEntities(
            List<com.bmagario.saga.order.service.domain.dto.create.OrderItem> items) {
        return items.stream().map(orderItem ->
                OrderItem.builder()
                        .product(new Product(new ProductId(orderItem.getProductId())))
                        .price(new Money(orderItem.getPrice()))
                        .quantity(orderItem.getQuantity())
                        .subtotal(new Money(orderItem.getSubtotal()))
                        .build()).collect(Collectors.toList());
    }

    private StreetAddress orderAddressToStreetAddress(OrderAddress address) {
        return new StreetAddress(
                UUID.randomUUID(),
                address.getStreet(),
                address.getPostalCode(),
                address.getCity()
        );
    }
}
