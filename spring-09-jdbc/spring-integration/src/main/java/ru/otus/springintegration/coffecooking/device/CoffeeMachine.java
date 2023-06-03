package ru.otus.springintegration.coffecooking.device;

import org.springframework.stereotype.Component;
import ru.otus.springintegration.coffecooking.Named;
import ru.otus.springintegration.coffecooking.product.AbstractProduct;
import ru.otus.springintegration.coffecooking.product.Coffee;
import ru.otus.springintegration.coffecooking.product.CoffeeBean;
import ru.otus.springintegration.coffecooking.product.FreshWater;

import java.util.List;

@Component
public class CoffeeMachine implements Named {

    private FreshWater freshWater;
    private CoffeeBean coffeeBean;

    @Override
    public String getName() {
        return "Coffee Machine";
    }

    public List<AbstractProduct> pourInFreshWater(List<AbstractProduct> ingredients) {

        if (freshWater != null) {
            report("Already full of fresh water!");
            return ingredients;
        }

        AbstractProduct toBeUsed = ingredients.stream().filter(FreshWater.class::isInstance).findAny().orElse(null);
        if (toBeUsed == null) {
            report("No Fresh Water in ingredients collection!");
            return ingredients;
        }

        ingredients.remove(toBeUsed);
        this.freshWater = (FreshWater) toBeUsed;

        report("Filling with " + toBeUsed.getName() + "...");

        return ingredients;
    }

    public List<AbstractProduct> pourInCoffeeBeans(List<AbstractProduct> ingredients) {

        if (coffeeBean != null) {
            report("Already full of coffee bean!");
            return ingredients;
        }

        AbstractProduct toBeUsed = ingredients.stream().filter(CoffeeBean.class::isInstance).findAny().orElse(null);
        if (toBeUsed == null) {
            report("No Coffee Bean in ingredients collection!");
            return ingredients;
        }

        ingredients.remove(toBeUsed);
        this.coffeeBean = (CoffeeBean) toBeUsed;

        report("Filling with " + toBeUsed.getName() + "...");

        return ingredients;
    }

    public boolean isReady() {
        return freshWater != null && coffeeBean != null;
    }

    public Coffee boil() {

        if (freshWater == null) {
            report("No Fresh Water!");
        }

        if (coffeeBean == null) {
            report("No Coffee Bean!");
        }

        report("Boiling water to make a cup of Black Coffee...");

        freshWater = null;
        coffeeBean = null;

        return new Coffee();
    }
}
