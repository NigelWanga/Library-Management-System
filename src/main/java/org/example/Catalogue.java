package org.example;

import java.util.ArrayList;

public class Catalogue {
    ArrayList<Book> catalogue;
    ArrayList<Book> books;

    public Catalogue(){
        catalogue = new ArrayList<>();
        books = new ArrayList<>();
    }

    public void addBook(Book book){
        catalogue.add(book);
    }

    Book getBook(int index){
        return catalogue.get(index);
    }

    ArrayList<Book> getAllBooks(){
        return books;
    }

    public Book getBookHeld(String title) {
        for (Book book : catalogue) {
            if (book.getTitle().equals(title)) {
                return book;
            }

        }
        return null;
    }

    public int getCatalogueSize(){
        return catalogue.size();
    }
}
