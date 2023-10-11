package com.bmagario.saga.order.service.dataaccess.restaurant.mapper;

import com.bmagario.saga.order.service.dataaccess.restaurant.entity.RestaurantEntity;
import com.bmagario.saga.order.service.dataaccess.restaurant.exception.RestaurantDataAccessException;
import com.bmagario.saga.order.service.domain.entity.Product;
import com.bmagario.saga.order.service.domain.entity.Restaurant;
import com.bmagario.saga.order.service.domain.valueobject.Money;
import com.bmagario.saga.order.service.domain.valueobject.ProductId;
import com.bmagario.saga.order.service.domain.valueobject.RestaurantId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class RestaurantDataAccessMapper {

    public List<UUID> restaurantToRestaurantProducts(Restaurant restaurant) {
        return restaurant.getProducts().stream()
                .map(product -> product.getId().getValue())
                .collect(Collectors.toList());
    }

    public Restaurant restaurantEntityToRestaurant(List<RestaurantEntity> restaurantEntities) {
        RestaurantEntity restaurantEntity =
                restaurantEntities.stream().findFirst().orElseThrow(() ->
                        new RestaurantDataAccessException("Restaurant could not be found!"));

        List<Product> restaurantProducts = restaurantEntities.stream().map(entity ->
                new Product(new ProductId(entity.getProductId()), entity.getProductName(),
                        new Money(entity.getProductPrice()))).toList();

        return Restaurant.builder()
                .restaurantId(new RestaurantId(restaurantEntity.getRestaurantId()))
                .products(restaurantProducts)
                .active(restaurantEntity.getRestaurantActive())
                .build();
    }
}
