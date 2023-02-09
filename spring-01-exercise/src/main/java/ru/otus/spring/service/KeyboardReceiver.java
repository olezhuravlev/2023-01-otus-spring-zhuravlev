package ru.otus.spring.service;

import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class KeyboardReceiver implements Receiver {

    @Override
    public String receive(String welcomeText) {
        System.out.println(welcomeText);
        Scanner keyboard = new Scanner(System.in);
        return keyboard.nextLine();
    }
}
