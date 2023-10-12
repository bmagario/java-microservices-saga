package com.bmagario.saga.order.service.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"com.bmagario.saga.order.service.dataaccess",
        "com.bmagario.saga.dataaccess"})
@EntityScan(basePackages = {"com.bmagario.saga.order.service.dataaccess",
        "com.bmagario.saga.dataaccess"})
@SpringBootApplication(scanBasePackages = "com.bmagario.saga")
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
