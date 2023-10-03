package com.bmagario.saga.order.server.domain.entity;

import com.bmagario.saga.order.server.domain.exception.OrderDomainException;
import com.bmagario.saga.order.server.domain.valueobject.CustomertId;
import com.bmagario.saga.order.server.domain.valueobject.Money;
import com.bmagario.saga.order.server.domain.valueobject.OrderId;
import com.bmagario.saga.order.server.domain.valueobject.OrderItemId;
import com.bmagario.saga.order.server.domain.valueobject.OrderStatus;
import com.bmagario.saga.order.server.domain.valueobject.RestaurantId;
import com.bmagario.saga.order.server.domain.valueobject.StreetAddress;
import com.bmagario.saga.order.server.domain.valueobject.TrackingId;
import java.util.List;
import java.util.UUID;

public class Order extends AggregateRoot<OrderId> {
    private final CustomertId customertId;
    private final RestaurantId restaurantId;
    private final StreetAddress deliveryAddress;
    private final Money price;
    private final List<OrderItem> items;
    private TrackingId trackingId;
    private OrderStatus orderStatus;
    private List<String> failureMeesages;

    public void initializeOrder() {
        setId(new OrderId(UUID.randomUUID()));
        trackingId = new TrackingId(UUID.randomUUID());
        orderStatus = OrderStatus.PENDING;
        initializeOrderItems();
    }

    public void validateOrder() {
        validateInitialOrder();
        validateTotalPrice();
        validateOrderItemsPrice();
    }

    private void validateInitialOrder() {
        if (orderStatus != null || getId() != null) {
            throw new OrderDomainException("Order is not in a correct state for initialization!");
        }
    }

    private void validateTotalPrice() {
        if (price == null || !price.isGreaterThanZero()) {
            throw new OrderDomainException("Total price must be greater than zero!");
        }
    }

    private void validateOrderItemsPrice() {
        Money orderItemsTotal = items.stream().map(orderItem -> {
            validateItemPrice(orderItem);
            return orderItem.getSubtotal();
        }).reduce(Money.ZERO, Money::add);

        if (!price.equals(orderItemsTotal)) {
            throw new OrderDomainException("Total price: " + price.getAmount() +
                    " is not equal to order items total price: " + orderItemsTotal.getAmount() +
                    "!");
        }
    }

    private void validateItemPrice(OrderItem orderItem) {
        if (!orderItem.isPriceValid()) {
            throw new OrderDomainException("Order item price: " + orderItem.getPrice().getAmount() +
                    " is not valid for product: " + orderItem.getProduct().getId().getValue() +
                    "!");
        }
    }

    private void initializeOrderItems() {
        long itemId = 1;
        for (OrderItem orderItem : items) {
            orderItem.initializeOrderItem(this.getId(), new OrderItemId(itemId));
            itemId++;
        }
    }

    private Order(Builder builder) {
        super.setId(builder.orderId);
        customertId = builder.customertId;
        restaurantId = builder.restaurantId;
        deliveryAddress = builder.deliveryAddress;
        price = builder.price;
        items = builder.items;
        trackingId = builder.trackingId;
        orderStatus = builder.orderStatus;
        failureMeesages = builder.failureMeesages;
    }


    public CustomertId getCustomertId() {
        return customertId;
    }

    public RestaurantId getRestaurantId() {
        return restaurantId;
    }

    public StreetAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public Money getPrice() {
        return price;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public TrackingId getTrackingId() {
        return trackingId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public List<String> getFailureMeesages() {
        return failureMeesages;
    }

    public static final class Builder {
        private OrderId orderId;
        private CustomertId customertId;
        private RestaurantId restaurantId;
        private StreetAddress deliveryAddress;
        private Money price;
        private List<OrderItem> items;
        private TrackingId trackingId;
        private OrderStatus orderStatus;
        private List<String> failureMeesages;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder orderId(OrderId val) {
            orderId = val;
            return this;
        }

        public Builder customertId(CustomertId val) {
            customertId = val;
            return this;
        }

        public Builder restaurantId(RestaurantId val) {
            restaurantId = val;
            return this;
        }

        public Builder deliveryAddress(StreetAddress val) {
            deliveryAddress = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }

        public Builder items(List<OrderItem> val) {
            items = val;
            return this;
        }

        public Builder trackingId(TrackingId val) {
            trackingId = val;
            return this;
        }

        public Builder orderStatus(OrderStatus val) {
            orderStatus = val;
            return this;
        }

        public Builder failureMeesages(List<String> val) {
            failureMeesages = val;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}
