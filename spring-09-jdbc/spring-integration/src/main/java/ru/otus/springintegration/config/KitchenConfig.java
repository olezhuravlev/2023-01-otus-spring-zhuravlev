package ru.otus.springintegration.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.Message;
import ru.otus.springintegration.coffecooking.Named;
import ru.otus.springintegration.coffecooking.device.Cappuccinator;
import ru.otus.springintegration.coffecooking.device.CoffeeCupSupplier;
import ru.otus.springintegration.coffecooking.device.CoffeeMachine;
import ru.otus.springintegration.coffecooking.product.CoffeeBean;
import ru.otus.springintegration.coffecooking.product.FreshWater;
import ru.otus.springintegration.coffecooking.product.Milk;
import ru.otus.springintegration.coffecooking.service.KitchenServiceImpl;

import java.util.List;

@Configuration
@Slf4j
public class KitchenConfig {

    @Autowired
    private CoffeeMachine coffeeMachine;

    @Autowired
    private Cappuccinator cappuccinator;

    @Autowired
    private CoffeeCupSupplier coffeeCupSupplier;

    @MessagingGateway("kitchenGateway")
    public interface KitchenGateway {
        @Gateway(requestChannel = "incomingIngredientsChannel", replyChannel = "cookedProductsChannel")
        Named cook(Message message);
    }

    @Bean
    public PublishSubscribeChannel incomingIngredientsChannel() {
        return MessageChannels.publishSubscribe().getObject();
    }

    @Bean
    public PublishSubscribeChannel cookedProductsChannel() {
        return MessageChannels.publishSubscribe().getObject();
    }

    @Bean
    public PublishSubscribeChannel coffeeMachineWaterChannel() {
        return MessageChannels.publishSubscribe().getObject();
    }

    @Bean
    public PublishSubscribeChannel coffeeMachineBeansChannel() {
        return MessageChannels.publishSubscribe().getObject();
    }

    @Bean
    public PublishSubscribeChannel coffeeMachineRunChannel() {
        return MessageChannels.publishSubscribe().getObject();
    }

    @Bean
    public PublishSubscribeChannel cappuccinatorMilkChannel() {
        return MessageChannels.publishSubscribe().getObject();
    }

    @Bean
    public PublishSubscribeChannel coffeeCupChannel() {
        return MessageChannels.publishSubscribe().getObject();
    }

    @Bean
    public PublishSubscribeChannel outgoingProductChannel() {
        return MessageChannels.publishSubscribe().getObject();
    }

    @Bean
    public IntegrationFlow pouringIngredientsFlow(KitchenServiceImpl kitchenService, PublishSubscribeChannel incomingIngredientsChannel) {

        return IntegrationFlow.from(incomingIngredientsChannel) // -> List<AbstractProduct>
                .handle(kitchenService, "informOrderReceived") // -> Message (header 'order') {List<Named>}
                .scatterGather(route -> route
                        .recipientFlow(subflow -> subflow
                                .filter(this::hasFreshWater)
                                .channel("coffeeMachineWaterChannel"))
                        .recipientFlow(subflow -> subflow
                                .filter(this::hasCoffeeBean)
                                .channel("coffeeMachineBeansChannel"))
                        .recipientFlow(subflow -> subflow
                                .filter(this::hasMilk)
                                .channel("cappuccinatorMilkChannel")))
                .get();
    }

    @Bean
    public IntegrationFlow coffeeMachineWaterFlow(CoffeeMachine coffeeMachine, PublishSubscribeChannel coffeeMachineWaterChannel, PublishSubscribeChannel coffeeMachineRunChannel) {
        return IntegrationFlow.from(coffeeMachineWaterChannel)
                .handle(coffeeMachine, "pourInFreshWater") // -> List<AbstractProduct>
                .channel(coffeeMachineRunChannel)
                .get();
    }

    @Bean
    public IntegrationFlow coffeeMachineBeansFlow(CoffeeMachine coffeeMachine, PublishSubscribeChannel coffeeMachineBeansChannel, PublishSubscribeChannel coffeeMachineRunChannel) {
        return IntegrationFlow.from(coffeeMachineBeansChannel)
                .handle(coffeeMachine, "pourInCoffeeBeans") // -> List<AbstractProduct>
                .channel(coffeeMachineRunChannel)
                .get();
    }

    @Bean
    public IntegrationFlow coffeeMachineRunFlow(CoffeeMachine coffeeMachine, PublishSubscribeChannel coffeeMachineRunChannel, PublishSubscribeChannel coffeeCupChannel) {
        return IntegrationFlow.from(coffeeMachineRunChannel)
                .aggregate(a -> a.releaseStrategy(coffeeMachine, "isReady"))
                .handle(coffeeMachine, "boil") // -> Coffee
                .channel(coffeeCupChannel)
                .get();
    }

    @Bean
    public IntegrationFlow cappuccinatorMilkFlow(Cappuccinator cappuccinator, PublishSubscribeChannel cappuccinatorMilkChannel, PublishSubscribeChannel coffeeCupChannel) {
        return IntegrationFlow.from(cappuccinatorMilkChannel)
                .handle(cappuccinator, "pourInMilk") // -> List<AbstractProduct>
                .aggregate(a -> a.releaseStrategy(cappuccinator, "isReady"))
                .handle(cappuccinator, "frothMilk") // -> FrothedMilk
                .channel(coffeeCupChannel)
                .get();
    }

    @Bean
    public IntegrationFlow coffeeCupFlow(CoffeeCupSupplier coffeeCupSupplier, PublishSubscribeChannel coffeeCupChannel, PublishSubscribeChannel cookedProductsChannel) {
        return IntegrationFlow.from(coffeeCupChannel) // Coffee, FrothedMilk
                .handle(coffeeCupSupplier, "pourIn") // -> CoffeeCupSupplier
                .aggregate(a -> a.releaseStrategy(coffeeCupSupplier, "isFull"))
                .handle(coffeeCupSupplier, "giveAwayCoffeeCup") // -> CoffeeCupSupplier
                .channel(cookedProductsChannel)
                .get();
    }

    private boolean hasFreshWater(Object list) {
        return ((List<Named>) list).stream().anyMatch(FreshWater.class::isInstance);
    }

    private boolean hasCoffeeBean(Object list) {
        return ((List<Named>) list).stream().anyMatch(CoffeeBean.class::isInstance);
    }

    private boolean hasMilk(Object list) {
        return ((List<Named>) list).stream().anyMatch(Milk.class::isInstance);
    }
}
