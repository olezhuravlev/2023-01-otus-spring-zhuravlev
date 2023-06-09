package ru.otus.springintegration.coffecooking.product;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class FreshWater extends AbstractProduct {

    @Override
    public String getName() {
        return "Fresh water";
    }
}
