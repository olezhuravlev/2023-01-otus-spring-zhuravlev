package ru.otus.springintegration.coffecooking.product;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.otus.springintegration.coffecooking.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Data
public class CoffeeCup implements Named {

    private List<AbstractProduct> content = new ArrayList<>();

    @Override
    public String getName() {
        return "Coffee Cup";
    }

    public CoffeeCup pourIn(AbstractProduct product) {

        if (content.size() >= 2) {
            content.clear();
        }

        report("Pouring in " + product.getName() + "...");
        content.add(product);
        return this;
    }

    public boolean isFull() {
        return content.size() >= 2;
    }

    @Override
    public String toString() {
        return getName() + " with content: " + content.stream()
                .map(Named::getName)
                .collect(Collectors.joining(" and "));
    }
}
