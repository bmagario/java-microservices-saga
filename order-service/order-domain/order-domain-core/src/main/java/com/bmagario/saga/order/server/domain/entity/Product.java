package com.bmagario.saga.order.server.domain.entity;

import com.bmagario.saga.order.server.domain.valueobject.Money;
import com.bmagario.saga.order.server.domain.valueobject.ProductId;

public class Product extends BaseEntity<ProductId> {
    private String name;
    private Money price;

    public Product(ProductId productId, String name, Money price) {
        super.setId(productId);
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }
}
