package com.bmagario.saga.order.service.dataaccess.restaurant.exception;

public class RestaurantDataAccessException extends RuntimeException {
    public RestaurantDataAccessException(String message) {
        super(message);
    }
}
