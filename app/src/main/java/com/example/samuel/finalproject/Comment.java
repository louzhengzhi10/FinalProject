package com.example.samuel.finalproject;

/**
 * Created by mengxiongliu on 27/11/2016.
 */

public class Comment {
    private String user;
    private String date;
    private String comment;

    public Comment(String user, String date, String comment) {
        this.user = user;
        this.date = date;
        this.comment = comment;
    }

    public String getUser() {
        return user;
    }

    public String getDate() {
        return date;
    }

    public String getComment() {
        return comment;
    }
}
