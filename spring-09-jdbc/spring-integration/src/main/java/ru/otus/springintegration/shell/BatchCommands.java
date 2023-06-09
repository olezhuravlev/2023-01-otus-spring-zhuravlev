package ru.otus.springintegration.shell;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.springintegration.coffecooking.service.CoffeeshopService;

@RequiredArgsConstructor
@ShellComponent
@Slf4j
public class BatchCommands {

    @Autowired
    private CoffeeshopService coffeeshopService;

    @ShellMethod(value = "Cook a cap of milk coffee", key = {"coffee", "c"})
    public void cookMilkCoffee() {
        coffeeshopService.cookCoffee();
    }
}
