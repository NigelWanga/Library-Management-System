package org.example;

import java.util.ArrayList;

public class Catalogue {
    ArrayList<Book> catalogue;

    public Catalogue(){ catalogue = new ArrayList<>(); }
    public void addBook(Book book){
        catalogue.add(book);
    }
    Book getBook(int index){ return catalogue.get(index); }
    ArrayList<Book> getAllBooks(){ return catalogue; }

    public void displayAllBooks(){
        if (catalogue.isEmpty()) {
            System.out.println("No books available");
        } else {
            for (Book book : catalogue) {
                System.out.println(book.getTitle());
            }
        }
    }
    public int getCatalogueSize(){ return catalogue.size(); }

    //get book held by title
    public Book getBookHeld(String title) {
        for (Book book : catalogue) {
            if (book.getTitle().equals(title)) {
                return book;
            }
        }
        return null;
    }

    //the above getBookHeld shares a similar impl as to findBookByTitle, thus I just used it semantically


    //searchin for book by title
    public Book findBookByTitle(String title) {
        for (Book book : catalogue) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                return book;
            }
        }
        return null;
    }
}
