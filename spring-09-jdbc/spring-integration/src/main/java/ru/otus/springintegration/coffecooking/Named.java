package ru.otus.springintegration.coffecooking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Named {

    Logger LOG = LoggerFactory.getLogger(Named.class);

    String getName();

    default void report(String message) {
        LOG.info("===> " + getName() + ": " + message);
    }
}
