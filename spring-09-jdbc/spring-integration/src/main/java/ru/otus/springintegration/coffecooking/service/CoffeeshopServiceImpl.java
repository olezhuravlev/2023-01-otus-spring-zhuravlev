package ru.otus.springintegration.coffecooking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.springintegration.coffecooking.Named;
import ru.otus.springintegration.coffecooking.model.Order;
import ru.otus.springintegration.coffecooking.product.Coffee;
import ru.otus.springintegration.config.CoffeeShopConfig.CoffeeShopGateway;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

@Slf4j
@Service
public class CoffeeshopServiceImpl implements CoffeeshopService {

    private final CoffeeShopGateway coffeeshopGateway;

    public CoffeeshopServiceImpl(CoffeeShopGateway coffeeshopGateway) {
        this.coffeeshopGateway = coffeeshopGateway;
    }

    @Override
    public void cookCoffee() {

        ForkJoinPool pool = ForkJoinPool.commonPool();
        pool.execute(() -> {
            List<Order> orders = createOrders(1);
            // Invokes methods sequence,  specified in IntegrationFlow config.
            List<Named> result = coffeeshopGateway.process(orders);
            reportResult(result);
        });
    }

    private List<Order> createOrders(int quantity) {

        List<Order> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            result.add(new Order(new Coffee(), 1));
        }
        return result;
    }

    private void reportResult(List<Named> result) {

        if (result == null) {
            log.info("===> NO RESULT!");
            return;
        }

        for (Named named : result) {
            log.info("===> PRODUCT COOKED: " + named.toString() + ".");
        }
    }
}
