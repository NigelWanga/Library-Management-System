package org.example;

public class Book {
    String title;
    String author;

    Book(String title, String author){
        this.title = title;
        this.author = author;
    }
    public String getTitle(){
        return title;
    }
}
