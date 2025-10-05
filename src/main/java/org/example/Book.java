package org.example;

public class Book {
    String title;
    String author;
    boolean isBorrowed;

    Book(String title, String author){
        this.title = title;
        this.author = author;
        isBorrowed = false;
    }
    public String getTitle(){
        return title;
    }
    public String getAuthor(){ return author; }

    public boolean isAvailable() { return !isBorrowed; }
    public void borrowBook() { isBorrowed = true; }
    public void returnBook() { isBorrowed = false;}
    public void setBorrowed(boolean borrowed) {
        this.isBorrowed = borrowed;
    }
}
