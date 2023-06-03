package ru.otus.springintegration.coffecooking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import ru.otus.springintegration.coffecooking.Named;
import ru.otus.springintegration.coffecooking.model.Order;
import ru.otus.springintegration.coffecooking.product.AbstractProduct;
import ru.otus.springintegration.coffecooking.product.Coffee;

import java.util.List;
import java.util.stream.Collectors;

import static ru.otus.springintegration.config.CoffeeShopConfig.ORDER_HEADER_NAME;

@Slf4j
@Service
public class KitchenServiceImpl implements KitchenService {

    @Override
    public Message informOrderReceived(Message message) {

        Order order = (Order) message.getHeaders().get(ORDER_HEADER_NAME);
        List<AbstractProduct> ingredients = (List<AbstractProduct>) message.getPayload();

        Named item = order.getItem();
        if (item == null) {
            log.info("===> NO ITEM!");
            return message;
        }

        if (item instanceof Coffee) {
            log.info("===> Order for cooking " + item.getName() + " has been received by the kitchen having the following ingredients:\n"
                    + ingredients.stream()
                    .map(e -> " - " + e.toString())
                    .collect(Collectors.joining(";\n")));
            return message;
        }

        return message;
    }
}
