package ru.otus.spring.service;

import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
public class UserProvider {

    public String getUserName(String welcomeText) {

        Scanner scanner = new Scanner(System.in);

        String userName = "";
        while (userName.isBlank()) {
            System.out.println(welcomeText);
            userName = scanner.nextLine();
        }

        return userName;
    }
}
