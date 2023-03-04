package ru.otus.spring.shell;

import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
public class CLIValueProvider {

    public String getValue(String welcomeText) {

        Scanner scanner = new Scanner(System.in);

        String value = "";
        while (value.isBlank()) {
            System.out.println(welcomeText);
            value = scanner.nextLine();
        }

        return value;
    }
}
