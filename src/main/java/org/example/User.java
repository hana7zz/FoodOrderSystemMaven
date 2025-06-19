package org.example;

public class User {
    private int id; // This is the user id.
    public String roomNumber; // This is the user roomNumber.
    public String date;
    public String foodNumber;
    public String time;
    public String price;

    public User() {}

    public User(String roomNumber, String date, String foodNumber, String time, String price) {
        this.roomNumber = roomNumber;
        this.date = date;
        this.foodNumber = foodNumber;
        this.time = time;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFoodNumber() {
        return foodNumber;
    }

    public void setFoodNumber(String foodNumber) {
        this.foodNumber = foodNumber;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
