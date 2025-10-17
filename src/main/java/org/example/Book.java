package org.example;

import java.time.LocalDate;

public class Book {
    String title;
    String author;
    boolean isBorrowed;
    boolean isOnHold;
    LocalDate dueDate;
    String onHoldBy;
    String status;

    Book(String title, String author){
        this.title = title;
        this.author = author;
        isBorrowed = false;
        isOnHold = false;
        dueDate = null;
        onHoldBy = null;
    }
    public String getTitle(){
        return title;
    }
    public String getAuthor(){ return author; }

    public boolean isAvailable() { return !isBorrowed; }

    //setting the max borrow period as 14 days
    public void borrowBook() {
        isBorrowed = true;
        dueDate = LocalDate.now().plusDays(14);
    }

    public void returnBook() {
        isBorrowed = false;
        dueDate = null;
        isOnHold = false; //hold cleared when returned
    }

    public void setBorrowed(boolean borrowed) { this.isBorrowed = borrowed; }
    public boolean isOnHold() { return onHoldBy != null; }
    public boolean isBorrowed() { return isBorrowed; }
    public void setOnHold(boolean hold) { isOnHold = hold; }
    public LocalDate getDueDate() { return dueDate; }
    public void placeHold(String borrowerUser) { onHoldBy = borrowerUser; } //changed - resp_22
    public String getHoldBy() { return onHoldBy; }

    public String getStatus() {
        if (isBorrowed) return "Checked out";
        if (isOnHold) return "On Hold";
        return "Available";
    }

    public void setStatus(String status) { this.status = status; }
}
