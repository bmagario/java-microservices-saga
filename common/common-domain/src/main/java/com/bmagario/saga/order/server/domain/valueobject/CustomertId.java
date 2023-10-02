package com.bmagario.saga.order.server.domain.valueobject;

import java.util.UUID;

public class CustomertId extends BaseId<UUID> {
    public CustomertId(UUID value) {
        super(value);
    }
}
