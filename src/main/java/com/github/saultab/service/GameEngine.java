package com.github.saultab.service;

import com.github.saultab.model.Box;
import com.github.saultab.model.PrizeTable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.*;

@ApplicationScoped
public class GameEngine {

    @Inject
    TurnManager turnManager;

    private Map<Integer, Box> boxes = new HashMap<>();
    private Box playerBox;

    public void initGame(long seed) {
        final var allValues = new ArrayList<>(PrizeTable.getAllPrizes());

        Random notaryRandom = new Random(seed);

        // Shuffle with special seed
        Collections.shuffle(allValues, notaryRandom);
        boxes.clear();
        for (int i = 0; i < 20; i++) {
            int id = i + 1;
            Integer value = allValues.get(i);
            boxes.put(id, new Box(id, value));
        }

        // Start next turn of 6 boxes
        turnManager.startNewTurn(6);
    }

    public void selectPlayerBox(int id) {
        this.playerBox = Optional.ofNullable(boxes.get(id))
                .orElseThrow(() -> new IllegalArgumentException("Pacco " + id + " non esistente"));
    }

    public boolean isBoxOpenable(int id) {
        Box box = boxes.get(id);

        // A box is openable if:
        // 1. Exist (id between 1 and 20)
        // 2. Is not opened
        // 3. Is not player box
        return box != null && !box.isOpened() && box != playerBox;
    }

    public void openBox(int id) {
        Box box = boxes.get(id);

        if (box == null) {
            throw new IllegalArgumentException("Il pacco numero " + id + " non esiste.");
        }

        if (box.isOpened()) {
            throw new IllegalStateException("Il pacco numero " + id + " è già stato aperto.");
        }

        box.setOpened(true);
        turnManager.registerBoxOpened();
    }

    public int getBoxValue(int id) {
        Box box = boxes.get(id);
        return (box != null) ? (int) box.getValue() : 0;
    }

    public Box getPlayerBox() {
        return playerBox;
    }

    public Map<Integer, Box> getAllBoxes() {
        return boxes;
    }

    public List<Box> getRemainingBoxes() {
        return boxes.values().stream()
                .filter(b -> !b.isOpened())
                .toList();
    }

    public List<Integer> getRemainingPrizes() {
        return boxes.values().stream()
                .filter(b -> !b.isOpened())
                .map(Box::getValue)
                .sorted()
                .toList();
    }

    public Integer getDoctorDecision() {
        Random random = new Random();

        // 25% chance they'll offer exchange instead of cash.
        if (random.nextInt(100) < 25) {
            return null;
        }

        int average = calculateAverage();

        // Evil Doctor: offer between 50% and 70% of average
        double factor = 0.5 + (random.nextDouble() * 0.2);
        double rawOffer = average * factor;
        int roundedOffer;

        if (rawOffer < 500) {
            // e.g. 320 -> 350
            roundedOffer = (int) (Math.ceil(rawOffer / 50.0) * 50);
        } else {
            // e.g. 1200 -> 1500
            roundedOffer = (int) (Math.ceil(rawOffer / 500.0) * 500);
        }
        return roundedOffer;
    }

    public int calculateAverage() {
        List<Box> remaining = getRemainingBoxes();
        if (remaining.isEmpty())
            return 0;

        double sum = remaining.stream()
                .mapToDouble(Box::getValue)
                .sum();

        return (int) (sum / remaining.size());
    }

    public void swapPlayerBox(Box oldBox, Box newBox) {
        this.playerBox = newBox;
    }
}