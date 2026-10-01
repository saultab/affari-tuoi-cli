package com.github.saultab.service;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TurnManager {
    private int boxesToOpenInCurrentTurn;
    private int boxesOpenedInCurrentTurn;

    public void startNewTurn(int boxesRequired) {
        this.boxesToOpenInCurrentTurn = boxesRequired;
        this.boxesOpenedInCurrentTurn = 0;
    }

    public void registerBoxOpened() {
        boxesOpenedInCurrentTurn++;
    }

    public boolean isTurnOver() {
        return boxesOpenedInCurrentTurn >= boxesToOpenInCurrentTurn;
    }

    public int getRemainingInTurn() {
        return boxesToOpenInCurrentTurn - boxesOpenedInCurrentTurn;
    }
}