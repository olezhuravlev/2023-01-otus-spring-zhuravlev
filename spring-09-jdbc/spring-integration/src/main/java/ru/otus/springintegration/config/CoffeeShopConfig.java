package ru.otus.springintegration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import ru.otus.springintegration.coffecooking.Named;
import ru.otus.springintegration.coffecooking.model.Order;
import ru.otus.springintegration.coffecooking.service.OrderInitializer;
import ru.otus.springintegration.coffecooking.service.StorageService;
import ru.otus.springintegration.config.KitchenConfig.KitchenGateway;

import java.util.List;

@Configuration
public class CoffeeShopConfig {

    public static final String ORDER_HEADER_NAME = "order";

    @MessagingGateway
    public interface CoffeeShopGateway {
        @Gateway(requestChannel = "incomingOrdersChannel")
        List<Named> process(List<Order> orders);
    }

    @Bean
    public QueueChannel incomingOrdersChannel() {
        return MessageChannels.queue().getObject();
    }

    @Bean
    public IntegrationFlow coffeeShopFlow(OrderInitializer initializer, StorageService storeService, KitchenGateway kitchenGateway) {

        return IntegrationFlow
                .from(incomingOrdersChannel()) // List<Order>
                .split()
                .handle(initializer, "initOrder") // -> Message (header 'order') {Order}
                .handle(storeService, "supplyIngredients") // -> Message (header 'order') {List<Named>}
                .handle(kitchenGateway, "cook") // -> Message (header 'order') {List<Named>}
                .aggregate()
                .get();
    }
}
