package ru.otus.springintegration.coffecooking.service;


import org.springframework.messaging.Message;

public interface KitchenService {

    Message informOrderReceived(Message message);
}
