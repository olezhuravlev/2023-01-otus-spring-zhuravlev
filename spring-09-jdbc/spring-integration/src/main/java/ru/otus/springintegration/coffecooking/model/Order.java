package ru.otus.springintegration.coffecooking.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import ru.otus.springintegration.coffecooking.Named;

@AllArgsConstructor
@Data
public class Order {
    private Named item;
    private int quantity;
}
