package ru.otus.springintegration.coffecooking.device;

import org.springframework.stereotype.Component;
import ru.otus.springintegration.coffecooking.Named;
import ru.otus.springintegration.coffecooking.product.AbstractProduct;
import ru.otus.springintegration.coffecooking.product.FrothedMilk;
import ru.otus.springintegration.coffecooking.product.Milk;

import java.util.List;

@Component
public class Cappuccinator implements Named {

    private Milk milk;

    @Override
    public String getName() {
        return "Cappuccinator";
    }

    public List<AbstractProduct> pourInMilk(List<AbstractProduct> ingredients) {

        if (milk != null) {
            report("Already full of milk!");
            return ingredients;
        }

        AbstractProduct toBeUsed = ingredients.stream().filter(ingredient -> ingredient instanceof Milk).findAny().orElse(null);
        if (toBeUsed == null) {
            report("No Milk in ingredients collection!");
            return ingredients;
        }

        ingredients.remove(toBeUsed);
        this.milk = (Milk) toBeUsed;

        report("Filling with " + toBeUsed.getName() + "...");

        return ingredients;
    }

    public boolean isReady() {
        return milk != null;
    }

    public FrothedMilk frothMilk() {

        if (milk == null) {
            report("No milk!");
        }

        report("Frothing " + milk.getName() + "...");
        milk = null;

        return new FrothedMilk();
    }
}
