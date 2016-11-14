package com.example.samuel.finalproject;

/**
 * Created by mengxiongliu on 07/11/2016.
 */

public class Dish {
    private int id;
    private String name;
    private float price;
    private int restaurant_id;
    private String restaurant_name = null;

    public Dish(int id, String name, float price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Dish(int id, String name, float price, int restaurant_id, String restaurant_name) {
        this(id, name, price);
        this.restaurant_id = restaurant_id;
        this.restaurant_name = restaurant_name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public int getRestaurant_id() {
        return restaurant_id;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }
}
