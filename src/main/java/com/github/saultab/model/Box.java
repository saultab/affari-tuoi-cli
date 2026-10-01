package com.github.saultab.model;

public class Box {
    private final int id;
    private final Integer value;
    private boolean opened;

    public Box(int id, Integer value) {
        this.id = id;
        this.value = value;
        this.opened = false;
    }

    public int getId() { return id; }
    public Integer getValue() { return value; }
    public boolean isOpened() { return opened; }
    public void setOpened(boolean opened) { this.opened = opened; }
    
}