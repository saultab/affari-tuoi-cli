package com.github.saultab.ui;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.NoSuchElementException;
import java.util.Scanner;

@ApplicationScoped
public class InputProvider {
    private final Scanner scanner = new Scanner(System.in);

    public String readNext() {
        try {
            if (scanner.hasNext()) {
                String input = scanner.next();

                if ("q".equalsIgnoreCase(input) || "exit".equalsIgnoreCase(input)) {
                    System.out.println("Uscita in corso... Grazie per aver giocato!");
                    System.exit(0);
                }
                return input;
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            return "";
        }
        return "";
    }
}