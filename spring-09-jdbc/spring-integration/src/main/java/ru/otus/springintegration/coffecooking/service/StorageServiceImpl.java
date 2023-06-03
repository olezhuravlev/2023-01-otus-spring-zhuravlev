package ru.otus.springintegration.coffecooking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import ru.otus.springintegration.coffecooking.Named;
import ru.otus.springintegration.coffecooking.model.Order;
import ru.otus.springintegration.coffecooking.product.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StorageServiceImpl implements StorageService {

    @Override
    public List<AbstractProduct> supplyIngredients(Message message) {

        Order order = (Order) message.getPayload();

        Named item = order.getItem();

        if (item instanceof Coffee) {
            int quantity = order.getQuantity();
            return supplyCoffeeIngredients(item, quantity);
        } else {
            // Unknown item ordered - no ingredients supplied.
            return new ArrayList<>();
        }
    }

    private List<AbstractProduct> supplyCoffeeIngredients(Named item, int quantity) {

        List<AbstractProduct> result = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {
            result.add(new CoffeeBean());
            result.add(new FreshWater());
            result.add(new Milk());
        }

        log.info("===> To make " + item.getName() + " the following items supplied:\n" + result.stream()
                .collect(Collectors.groupingBy(Named::getName, Collectors.summarizingInt(x -> 1)))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue().getCount())))
                .entrySet().stream()
                .map(e -> " - " + e.toString())
                .collect(Collectors.joining(";\n")));

        return result;
    }
}
