package com.bmagario.saga.order.service.domain.entity;

import com.bmagario.saga.order.service.domain.valueobject.Money;
import com.bmagario.saga.order.service.domain.valueobject.OrderId;
import com.bmagario.saga.order.service.domain.valueobject.OrderItemId;

public class OrderItem extends BaseEntity<OrderItemId> {
    private OrderId orderId;
    private final Product product;
    private final Integer quantity;
    private final Money price;
    private final Money subtotal;

    private OrderItem(Builder builder) {
        setId(builder.orderItemId);
        product = builder.product;
        quantity = builder.quantity;
        price = builder.price;
        subtotal = builder.subtotal;
    }


    public OrderId getOrderId() {
        return orderId;
    }

    public Product getProduct() {
        return product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Money getPrice() {
        return price;
    }

    public Money getSubtotal() {
        return subtotal;
    }

    void initializeOrderItem(OrderId orderId, OrderItemId orderItemId) {
        this.orderId = orderId;
        setId(orderItemId);
    }

    boolean isPriceValid() {
        return price.isGreaterThanZero()
                && price.equals(product.getPrice())
                && price.multiply(quantity).equals(subtotal);
    }

    public static Builder builder() {
        return new Builder();
    }


    public static final class Builder {
        private OrderItemId orderItemId;
        private Product product;
        private Integer quantity;
        private Money price;
        private Money subtotal;

        private Builder() {
        }


        public Builder orderItemId(OrderItemId val) {
            orderItemId = val;
            return this;
        }

        public Builder product(Product val) {
            product = val;
            return this;
        }

        public Builder quantity(Integer val) {
            quantity = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }

        public Builder subtotal(Money val) {
            subtotal = val;
            return this;
        }

        public OrderItem build() {
            return new OrderItem(this);
        }
    }
}
