package ru.otus.springintegration.coffecooking.device;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.otus.springintegration.coffecooking.Named;
import ru.otus.springintegration.coffecooking.product.AbstractProduct;
import ru.otus.springintegration.coffecooking.product.CoffeeCup;

import java.util.List;

@Slf4j
@Component
public class CoffeeCupSupplier implements Named {

    private CoffeeCup coffeeCup;

    @Override
    public String getName() {
        return "Coffee Cup Supplier";
    }

    public CoffeeCup pourIn(AbstractProduct product) {

        if (this.coffeeCup == null) {
            this.coffeeCup = new CoffeeCup();
            report("New " + this.coffeeCup.getName() + " supplied.");
        }

        report("Pouring " + product.getName() + " into " + this.coffeeCup.getName() + "...");
        List<AbstractProduct> content = coffeeCup.getContent();

        content.add(product);
        return coffeeCup;
    }

    public boolean isFull() {
        return this.coffeeCup.getContent().size() >= 2;
    }

    public CoffeeCup giveAwayCoffeeCup(CoffeeCup coffeeCup) {
        report("Giving away " + this.coffeeCup.getName() + "...");
        this.coffeeCup = null;
        return coffeeCup;
    }
}
