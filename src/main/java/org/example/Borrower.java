package org.example;

import java.util.ArrayList;

public class Borrower {
    String username;
    String password;
    ArrayList<String> borrowedBooks;

    public Borrower(String username, String password){
        this.username = username;
        this.password = password;
        this.borrowedBooks = new ArrayList<>();
    }

    public String getUsername() {return username; }

    public String getPassword() {return password; }

    public int getBorrowedBooksCount() {return borrowedBooks.size(); }

}
