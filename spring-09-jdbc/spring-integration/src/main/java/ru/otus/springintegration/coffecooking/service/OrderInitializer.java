package ru.otus.springintegration.coffecooking.service;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import ru.otus.springintegration.coffecooking.model.Order;

@Service
public class OrderInitializer {

    public Message initOrder(Message<Order> message) {

        // Put order in message header to make it accessible for all steps.
        return MessageBuilder
                .withPayload(message.getPayload())
                .copyHeaders(message.getHeaders())
                .setHeader("order", message.getPayload())
                .build();
    }
}
