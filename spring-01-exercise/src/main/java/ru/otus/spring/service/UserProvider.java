package ru.otus.spring.service;

import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
public class UserProvider {

    private String userName = "";

    public String getUserName() {

        Scanner scanner = new Scanner(System.in);

        while (userName.isBlank()) {
            System.out.println("Enter your name:");
            userName = scanner.nextLine();
        }

        return userName;
    }
}
