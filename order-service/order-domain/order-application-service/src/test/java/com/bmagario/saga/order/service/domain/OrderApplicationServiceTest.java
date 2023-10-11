package com.bmagario.saga.order.service.domain;

import com.bmagario.saga.order.service.domain.dto.create.CreateOrderCommand;
import com.bmagario.saga.order.service.domain.dto.create.CreateOrderResponse;
import com.bmagario.saga.order.service.domain.dto.create.OrderAddress;
import com.bmagario.saga.order.service.domain.dto.create.OrderItem;
import com.bmagario.saga.order.service.domain.entity.Customer;
import com.bmagario.saga.order.service.domain.entity.Order;
import com.bmagario.saga.order.service.domain.entity.Product;
import com.bmagario.saga.order.service.domain.entity.Restaurant;
import com.bmagario.saga.order.service.domain.exception.OrderDomainException;
import com.bmagario.saga.order.service.domain.mapper.OrderDataMapper;
import com.bmagario.saga.order.service.domain.ports.input.service.OrderApplicationService;
import com.bmagario.saga.order.service.domain.ports.output.repository.CustomerRepository;
import com.bmagario.saga.order.service.domain.ports.output.repository.OrderRepository;
import com.bmagario.saga.order.service.domain.ports.output.repository.RestaurantRepository;
import com.bmagario.saga.order.service.domain.valueobject.CustomerId;
import com.bmagario.saga.order.service.domain.valueobject.Money;
import com.bmagario.saga.order.service.domain.valueobject.OrderId;
import com.bmagario.saga.order.service.domain.valueobject.OrderStatus;
import com.bmagario.saga.order.service.domain.valueobject.ProductId;
import com.bmagario.saga.order.service.domain.valueobject.RestaurantId;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrderTestConfiguration.class)
public class OrderApplicationServiceTest {
    @Autowired
    private OrderApplicationService orderApplicationService;
    @Autowired
    private OrderDataMapper orderDataMapper;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    private CreateOrderCommand createOrderCommand;
    private CreateOrderCommand createOrderCommandWrongPrice;
    private CreateOrderCommand createOrderCommandWrongProductPrice;
    private final UUID ORDER_ID = UUID.fromString("5eddc8dd-4c4f-49b1-862e-2bf007f15145");
    private final UUID CUSTOMER_ID = UUID.fromString("bd2b29c2-52f0-4add-b1cc-e6aec981e22a");
    private final UUID RESTAURANT_ID = UUID.fromString("e6106816-6750-4ab2-900c-05deef6a78c3");
    private final UUID PRODUCT_ID = UUID.fromString("25239e81-c90a-40f8-a0e5-bfe414b6c837");
    private final BigDecimal PRICE = new BigDecimal("200.00");

    @BeforeAll
    public void init() {
        createOrderCommand = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .price(PRICE)
                .items(List.of(
                                OrderItem.builder()
                                        .productId(PRODUCT_ID)
                                        .price(new BigDecimal("50.00"))
                                        .quantity(1)
                                        .subtotal(new BigDecimal("50.00"))
                                        .build(),
                                OrderItem.builder()
                                        .productId(PRODUCT_ID)
                                        .price(new BigDecimal("50.00"))
                                        .quantity(3)
                                        .subtotal(new BigDecimal("150.00"))
                                        .build()
                        )
                )
                .address(OrderAddress.builder()
                        .street("Street Fighter")
                        .postalCode("1990B")
                        .city("Tokyo")
                        .build())
                .build();

        createOrderCommandWrongPrice = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .price(new BigDecimal("250.00"))
                .items(List.of(
                                OrderItem.builder()
                                        .productId(PRODUCT_ID)
                                        .price(new BigDecimal("50.00"))
                                        .quantity(1)
                                        .subtotal(new BigDecimal("50.00"))
                                        .build(),
                                OrderItem.builder()
                                        .productId(PRODUCT_ID)
                                        .price(new BigDecimal("50.00"))
                                        .quantity(3)
                                        .subtotal(new BigDecimal("150.00"))
                                        .build()
                        )
                )
                .address(OrderAddress.builder()
                        .street("Street Fighter")
                        .postalCode("1990B")
                        .city("Tokyo")
                        .build())
                .build();

        createOrderCommandWrongProductPrice = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .price(new BigDecimal("210.00"))
                .items(List.of(
                                OrderItem.builder()
                                        .productId(PRODUCT_ID)
                                        .price(new BigDecimal("60.00"))
                                        .quantity(1)
                                        .subtotal(new BigDecimal("60.00"))
                                        .build(),
                                OrderItem.builder()
                                        .productId(PRODUCT_ID)
                                        .price(new BigDecimal("50.00"))
                                        .quantity(3)
                                        .subtotal(new BigDecimal("50.00"))
                                        .build()
                        )
                )
                .address(OrderAddress.builder()
                        .street("Street Fighter")
                        .postalCode("1990B")
                        .city("Tokyo")
                        .build())
                .build();

        Customer customer = new Customer();
        customer.setId(new CustomerId(CUSTOMER_ID));

        Restaurant restaurant = Restaurant.builder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(List.of(
                        new Product(
                                new ProductId(PRODUCT_ID),
                                "product 1",
                                new Money(new BigDecimal("50.00"))
                        ),
                        new Product(
                                new ProductId(PRODUCT_ID),
                                "product 2",
                                new Money(new BigDecimal("50.00"))
                        )
                ))
                .active(true)
                .build();

        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        order.setId(new OrderId(ORDER_ID));

        Mockito.when(customerRepository.findCustomer(CUSTOMER_ID))
                .thenReturn(Optional.of(customer));
        Mockito.when(restaurantRepository.findRestaurantInformation(
                        orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
                .thenReturn(Optional.of(restaurant));
        Mockito.when(orderRepository.save(Mockito.any(Order.class))).thenReturn(order);
    }

    @Test
    public void testCreateOrder() {
        CreateOrderResponse createOrderResponse =
                orderApplicationService.createOrder(createOrderCommand);
        Assertions.assertEquals(OrderStatus.PENDING, createOrderResponse.getOrderStatus());
        Assertions.assertEquals("Order Created Successfully!", createOrderResponse.getMessage());
        Assertions.assertNotNull(createOrderResponse.getOrderTrackingId());
    }

    @Test
    public void testCreateOrderWithWrongTotalPrice() {
        OrderDomainException orderDomainException =
                Assertions.assertThrows(OrderDomainException.class,
                        () -> orderApplicationService.createOrder(createOrderCommandWrongPrice));
        Assertions.assertEquals(
                "Total price: 250.00 is not equal to order items total price: 200.00!",
                orderDomainException.getMessage());
    }

    @Test
    public void testCreateOrderWithWrongProductPrice() {
        OrderDomainException orderDomainException =
                Assertions.assertThrows(OrderDomainException.class,
                        () -> orderApplicationService.createOrder(
                                createOrderCommandWrongProductPrice));
        Assertions.assertEquals(
                "Order item price: 60.00 is not valid for product: " + PRODUCT_ID +
                        "!", orderDomainException.getMessage());
    }

    @Test
    public void testCreateOrderWithPassiveRestaurant() {
        Restaurant restaurant = Restaurant.builder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(List.of(
                        new Product(
                                new ProductId(PRODUCT_ID),
                                "product 1",
                                new Money(new BigDecimal("50.00"))
                        ),
                        new Product(
                                new ProductId(PRODUCT_ID),
                                "product 2",
                                new Money(new BigDecimal("50.00"))
                        )
                ))
                .active(false)
                .build();
        Mockito.when(restaurantRepository.findRestaurantInformation(
                        orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
                .thenReturn(Optional.of(restaurant));
        OrderDomainException orderDomainException =
                Assertions.assertThrows(OrderDomainException.class,
                        () -> orderApplicationService.createOrder(createOrderCommand));
        Assertions.assertEquals("Restaurant with id " + RESTAURANT_ID +
                " is not active!", orderDomainException.getMessage()
        );
    }
}
