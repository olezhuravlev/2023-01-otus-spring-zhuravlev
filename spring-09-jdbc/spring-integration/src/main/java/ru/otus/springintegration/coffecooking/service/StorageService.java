package ru.otus.springintegration.coffecooking.service;


import org.springframework.messaging.Message;
import ru.otus.springintegration.coffecooking.product.AbstractProduct;

import java.util.List;

public interface StorageService {

    List<AbstractProduct> supplyIngredients(Message message);
}
