package com.bmagario.saga.order.service.messaging.listener.kafka;

import com.bmagario.saga.kafka.consumer.KafkaConsumer;
import com.bmagario.saga.kafka.order.avro.model.PaymentResponseAvroModel;
import com.bmagario.saga.order.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;
import com.bmagario.saga.order.service.domain.valueobject.PaymentStatus;
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
public class PaymentResponseKafkaListener implements KafkaConsumer<PaymentResponseAvroModel> {

    private final PaymentResponseMessageListener paymentResponseMessageListener;
    private final OrderMessagingDataMapper orderMessagingDataMapper;

    public PaymentResponseKafkaListener(
            PaymentResponseMessageListener paymentResponseMessageListener,
            OrderMessagingDataMapper orderMessagingDataMapper) {
        this.paymentResponseMessageListener = paymentResponseMessageListener;
        this.orderMessagingDataMapper = orderMessagingDataMapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.payment-consumer-group-id}",
            topics = "${order-service.payment-response-topic-name}")
    public void receive(@Payload List<PaymentResponseAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info(
                "{} number of payment responses received with keys:{}, partitions:{} and offsets: {}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString());

        messages.forEach(paymentResponseAvroModel -> {
            if (PaymentStatus.COMPLETED.equals(paymentResponseAvroModel.getPaymentStatus())) {
                log.info("Processing successful payment for order id: {}",
                        paymentResponseAvroModel.getOrderId());
                paymentResponseMessageListener.paymentCompleted(orderMessagingDataMapper
                        .paymentResponseAvroModelToPaymentResponse(paymentResponseAvroModel));
            } else if (
                    PaymentStatus.CANCELLED.equals(paymentResponseAvroModel.getPaymentStatus()) ||
                            PaymentStatus.FAILED.equals(
                                    paymentResponseAvroModel.getPaymentStatus())) {
                log.info("Processing unsuccessful payment for order id: {}",
                        paymentResponseAvroModel.getOrderId());
                paymentResponseMessageListener.paymentCancelled(orderMessagingDataMapper
                        .paymentResponseAvroModelToPaymentResponse(paymentResponseAvroModel));
            }
        });
    }
}