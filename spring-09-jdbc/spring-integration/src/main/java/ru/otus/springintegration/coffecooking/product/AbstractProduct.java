package ru.otus.springintegration.coffecooking.product;

import lombok.extern.slf4j.Slf4j;
import ru.otus.springintegration.coffecooking.Named;

@Slf4j
public abstract class AbstractProduct implements Named {

    protected String name;

    void pour() {
        log.info(getName() + "Pouring!");
    }
}
