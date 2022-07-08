package com.example.ecommerceapp.Models;

public class Products {

    public String pname;
    public String description;
    public String price;
    public String time;
    public String date;
    public String category;
    public String image;
    public String id;

    public Products() {
    }

    public Products(String pname, String description, String price, String time, String date, String category, String image, String id) {
        this.pname = pname;
        this.description = description;
        this.price = price;
        this.time = time;
        this.date = date;
        this.category = category;
        this.image = image;
        this.id = id;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
