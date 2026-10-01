package com.github.saultab.model;

import java.util.List;
import java.util.stream.Stream;

public class PrizeTable {
    public static final List<Integer> BLUE_PRIZES = List.of(
            0, 1, 5, 10, 20, 50, 75, 100, 200, 500);

    public static final List<Integer> RED_PRIZES = List.of(
            5000, 10000, 15000, 20000, 30000, 50000, 75000, 100000, 200000, 300000);

    public static List<Integer> getAllPrizes() {
        return Stream.concat(BLUE_PRIZES.stream(), RED_PRIZES.stream()).toList();
    }
}