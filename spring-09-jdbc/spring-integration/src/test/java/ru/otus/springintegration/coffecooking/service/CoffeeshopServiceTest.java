package ru.otus.springintegration.coffecooking.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.test.context.SpringIntegrationTest;
import ru.otus.springintegration.coffecooking.Named;
import ru.otus.springintegration.coffecooking.model.Order;
import ru.otus.springintegration.coffecooking.product.Coffee;
import ru.otus.springintegration.coffecooking.product.CoffeeCup;
import ru.otus.springintegration.coffecooking.product.FrothedMilk;
import ru.otus.springintegration.config.CoffeeShopConfig;


import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@SpringIntegrationTest
class CoffeeshopServiceTest {

    @Autowired
    private CoffeeShopConfig.CoffeeShopGateway coffeeShopGateway;

    private static final List<CoffeeCup> EXPECTED_COFFEE_CUP = new ArrayList<>();

    @BeforeAll
    public static void beforeAll() {

        CoffeeCup coffeeCup = new CoffeeCup();
        coffeeCup.getContent().add(new Coffee());
        coffeeCup.getContent().add(new FrothedMilk());

        EXPECTED_COFFEE_CUP.clear();
        EXPECTED_COFFEE_CUP.add(coffeeCup);
    }

    @Test
    void cookCoffee() {

        List<Order> orders = List.of(new Order(new Coffee(), 1));
        List<Named> result = coffeeShopGateway.process(orders);
        assertThat(result).containsExactlyInAnyOrderElementsOf(EXPECTED_COFFEE_CUP);
    }
}
