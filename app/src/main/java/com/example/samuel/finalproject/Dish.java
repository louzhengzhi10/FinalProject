package com.example.samuel.finalproject;

import java.io.Serializable;

/**
 * Created by mengxiongliu on 07/11/2016.
 */

public class Dish implements Serializable {
    private int id;
    private String name;
    private float price;
    private int restaurant_id;
    private String restaurant_name = null;
    private boolean liked;
    private String shared_by = null;
    private String message = null;
    private boolean notified;

    public Dish(int id, String name, float price, boolean liked) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.liked = liked;

    }

    public Dish(int id, String name, float price, int restaurant_id, String restaurant_name, boolean liked) {
        this(id, name, price, liked);
        this.restaurant_id = restaurant_id;
        this.restaurant_name = restaurant_name;
    }

    public Dish(int id, String name, float price, int restaurant_id, String restaurant_name, boolean liked, String shared_by, String message, boolean notified) {
        this(id, name, price, restaurant_id, restaurant_name, liked);
        this.shared_by = shared_by;
        this.message = message;
        this.notified = notified;
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

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public String getShared_by() {
        return shared_by;
    }

    public String getMessage() {
        return message;
    }

    public boolean isNotified() {
        return notified;
    }
}
