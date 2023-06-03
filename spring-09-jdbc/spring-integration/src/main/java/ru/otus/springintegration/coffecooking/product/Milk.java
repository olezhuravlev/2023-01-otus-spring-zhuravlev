package ru.otus.springintegration.coffecooking.product;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Milk extends AbstractProduct {

    @Override
    public String getName() {
        return "Milk";
    }
}
