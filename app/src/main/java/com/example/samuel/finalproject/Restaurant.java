package com.example.samuel.finalproject;

/**
 * Created by mengxiongliu on 06/11/2016.
 */

public class Restaurant {
    private int id;
    private String name;
    private String address;

    public Restaurant(int id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}
