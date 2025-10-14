package org.example;

import java.util.ArrayList;

public class Borrower {
    String username;
    String password;

    public ArrayList<String> borrowedBooks;
    public ArrayList<String> heldBooks;


    public Borrower(String username, String password){
        this.username = username;
        this.password = password;
        this.borrowedBooks = new ArrayList<>();
        this.heldBooks = new ArrayList<>();
    }

    public String getUsername() {return username; }
    public String getPassword() {return password; }
    public int getBorrowedBooksCount() {return borrowedBooks.size(); }
    public ArrayList<String> getBorrowedBooks() { return borrowedBooks; }
    public ArrayList<String> getHeldBooks() { return heldBooks;}
    public void addBorrowedBook(String title) { borrowedBooks.add(title); }
    public boolean hasHeldBook(String title) { return heldBooks.contains(title); }
    public void addHeldBook(String title) { heldBooks.add(title); }
    public void removeBorrowedBook(String title){ borrowedBooks.remove(title); }

}
