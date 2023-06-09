package ru.otus.springintegration.coffecooking.product;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CoffeeBean extends AbstractProduct {

    @Override
    public String getName() {
        return "Coffee bean";
    }
}
