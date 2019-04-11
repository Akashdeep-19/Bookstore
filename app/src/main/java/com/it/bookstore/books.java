package com.it.bookstore;

import java.io.Serializable;
import java.util.ArrayList;

public class books implements Serializable{
    public
    String title,author,course,imageUrl,user,id,genre,price,year;
    ArrayList<String>buyers = new ArrayList<>();
    books(){

    }
    books(String user ,String id ,String title,String author,String course,String imageUrl,String genre,String price,String year){
        this.user = user;
        this.id = id;
        this.title = title;
        this.author = author;
        this.course = course;
        this.price = price;
        this.year = year;
        this.genre = genre;
        this.imageUrl = imageUrl;

    }
    public void addBuyer(String buyer){
        buyers.add(buyer);
    }

    public void setBuyers(ArrayList<String> buyers){
        this.buyers = buyers;
    }

    public boolean hasBuyer(String buyer){
        return buyers.contains(buyer);
    }
}
