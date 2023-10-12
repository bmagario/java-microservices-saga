package com.bmagario.saga.order.service.messaging.listener.kafka;

import com.bmagario.saga.kafka.consumer.KafkaConsumer;
import com.bmagario.saga.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import com.bmagario.saga.order.service.domain.entity.Order;
import com.bmagario.saga.order.service.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import com.bmagario.saga.order.service.domain.valueobject.OrderApprovalStatus;
import com.bmagario.saga.order.service.messaging.mapper.OrderMessagingDataMapper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RestaurantApprovalResponseKafkaListener
        implements KafkaConsumer<RestaurantApprovalResponseAvroModel> {

    private final RestaurantApprovalResponseMessageListener
            restaurantApprovalResponseMessageListener;
    private final OrderMessagingDataMapper orderMessagingDataMapper;

    public RestaurantApprovalResponseKafkaListener(RestaurantApprovalResponseMessageListener
                                                           restaurantApprovalResponseMessageListener,
                                                   OrderMessagingDataMapper orderMessagingDataMapper) {
        this.restaurantApprovalResponseMessageListener = restaurantApprovalResponseMessageListener;
        this.orderMessagingDataMapper = orderMessagingDataMapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.restaurant-approval-consumer-group-id}",
            topics = "${order-service.restaurant-approval-response-topic-name}")
    public void receive(@Payload List<RestaurantApprovalResponseAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info(
                "{} number of restaurant approval responses received with keys {}, partitions {} and offsets {}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString());

        messages.forEach(restaurantApprovalResponseAvroModel -> {
            if (OrderApprovalStatus.APPROVED.equals(
                    restaurantApprovalResponseAvroModel.getOrderApprovalStatus())
            ) {
                log.info("Processing approved order for order id: {}",
                        restaurantApprovalResponseAvroModel.getOrderId());
                restaurantApprovalResponseMessageListener.orderApproved(orderMessagingDataMapper
                        .approvalResponseAvroModelToApprovalResponse(
                                restaurantApprovalResponseAvroModel));
            } else if (OrderApprovalStatus.REJECTED.equals(
                    restaurantApprovalResponseAvroModel.getOrderApprovalStatus())
            ) {
                log.info("Processing rejected order for order id: {}, with failure messages: {}",
                        restaurantApprovalResponseAvroModel.getOrderId(),
                        String.join(Order.FAILURE_MESSAGE_DELIMITER,
                                restaurantApprovalResponseAvroModel.getFailureMessages()));
                restaurantApprovalResponseMessageListener.orderRejected(orderMessagingDataMapper
                        .approvalResponseAvroModelToApprovalResponse(
                                restaurantApprovalResponseAvroModel));
            }
        });

    }
}