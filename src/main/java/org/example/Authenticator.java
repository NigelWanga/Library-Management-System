package org.example;

import java.util.Scanner;

public class Authenticator {

    private BorrowerRegistry borrowerRegistry;
    private Borrower currentUser;
    private boolean authenticationPrompt = false;

    public Authenticator(BorrowerRegistry borrowerRegistry) {
        this.borrowerRegistry = borrowerRegistry;
    }

    //prompt for credentials
    public void promptCredentials() {
        System.out.print("Enter username: ");
        System.out.print("Enter password: ");
    }


    //Capture borrower credentials
    public String[] captureCredentials() {
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();
        String password = scanner.nextLine();
        return new String[]{username, password};
    }


    //validate borrower credentials
    public boolean validateCredentials(String username, String password) {
        Borrower borrower = borrowerRegistry.findBorrowerUsername(username);
        if (borrower == null) { return false; }
        return borrower.getPassword().equals(password);
    }


    public boolean handleInvalidLogin(String username, String password) {
        boolean isValid = validateCredentials(username, password);

        if (!isValid) {
            System.out.println("Authentication failed: Invalid username or password");
            System.out.println("Please try again");
            return false;
        }

        System.out.println("Authentication successful");
        return true;
    }

    public boolean login(String username, String password) {
        if (validateCredentials(username, password)) {
            currentUser = borrowerRegistry.findBorrowerUsername(username);
            return true;
        } else {
            currentUser = null;
            return false;
        }
    }

    public String checkAvailableHolds(Borrower borrower, Catalogue catalogue) {
        for (String heldTitle : borrower.getHeldBooks()) {
            Book heldBook = catalogue.getBookHeld(heldTitle);
            if (heldBook != null && heldBook.isAvailable()) {
                return "Book available: " + heldBook.getTitle() + "by " + heldBook.getAuthor();
            }
        }
        return null;
    }

    public String displayAvailableOperations(Borrower borrower) {
        return "Available operations: Borrow | Return | Logout";
    }

    public String displayBookStatus(Book book, Borrower borrower) {
        if (book.isOnHold() && !borrower.getHeldBooks().contains(book.getTitle())) {
            return "On Hold"; //another borrower has it
        }
        return book.getStatus();
    }

    public Borrower getCurrentUser() { return currentUser; }


    public Book selectAvailableBook(Catalogue catalogue) {
        for (Book book : catalogue.getAllBooks()) {
            if (book.isAvailable()) {
                return book; //this is when borrower selects first available book
            }
        }
        return null;
    }

    public String presentBorrowingDetails(Book selectedBook) {
        if (selectedBook == null) return "No book selected";

        return "Selected Book Details:\n" +
                "Title: " + selectedBook.getTitle() + "\n" +
                "Author: " + selectedBook.getAuthor() + "\n" +
                "Status: " + selectedBook.getStatus();
    }

    public String confirmBorrowing(Book selectedBook, Borrower borrower) {
        if (selectedBook == null) return "No book selected for confirmation";
        if (!selectedBook.isAvailable()) return "Book is not available";

        //max borrow limit
        if (borrower.getBorrowedBooks().size() >= 3) {
            return "Maximum borrowing limit reached";
        }

        selectedBook.borrowBook(); //the selected book is borrowed
        borrower.addBorrowedBook(selectedBook.getTitle());

        return "Borrow confirmed: " + selectedBook.getTitle() + "\nDue Date: " + selectedBook.getDueDate();
    }

    public boolean verifyBookAvailability(Book book, Borrower borrower) {
        return book.isAvailable() && !(book.isOnHold() && !borrower.getHeldBooks().contains(book.getTitle()));
    }

    public boolean verifyBorrowerEligibility(Borrower borrower) {
        return borrower.getBorrowedBooksCount() < 3; //borrower can borrow if has < 3 books
    }

    public void updateBorrowerAndBook(Borrower borrower, Book book) {
        if (book != null && book.isAvailable()) {
            book.borrowBook();

            borrower.addBorrowedBook(book.getTitle());
        }
    }

    public boolean acknowledgeBorrowing(Book book, Borrower borrower) { return true; }

    public boolean returnToFunctionalitySection() { return true; }

    public String handleUnavailableBook(Book book, Borrower borrower) {
        if (book == null) return "Book not found.";

        if (borrower.getBorrowedBooks().contains(book.getTitle()))
            return "You already have this book checked out";

        if (borrower.hasHeldBook(book.getTitle()))
            return "You already have a hold on this book";

        if (!book.isAvailable()) {
            book.placeHold(borrower.getUsername());
            borrower.addHeldBook(book.getTitle());
            return "Hold placed successfully.";
        }

        if (borrower.getBorrowedBooksCount() >= 3) {
            book.placeHold(borrower.getUsername());
            borrower.addHeldBook(book.getTitle());
            return "You reached your limit, but a hold was placed.";
        }

        return "Book is available — proceed to borrow.";
    }


    public String displayBorrowedBooks(Borrower borrower, Catalogue catalogue) {
        if (borrower.getBorrowedBooks().isEmpty()) {
            return "No books are currently borrowed";
        }

        String display = "";
        for (String title : borrower.getBorrowedBooks()) {
            Book book = catalogue.getBookHeld(title);
            if (book != null) {
                display += book.getTitle() + " - Due: " + book.getDueDate().toString() + "\n";
            }
        }
        return display.trim();
    }

    public String returnBook(Book book, Borrower borrower, Catalogue catalogue) {
        if (borrower.getBorrowedBooks().isEmpty()) {
            return "No books are currently borrowed";
        }

        //check for holds
        if (book == null) {
            return "No book selected for return";
        }

        //update status & borrower acc
        if (book.getHoldBy() == null || book.getHoldBy().isEmpty()) {
            book.returnBook(); //reset borrowed, onHold, dueDate
        } else {
            book.setBorrowed(false);
            book.setOnHold(true);
            book.setStatus("On Hold");
        }

        borrower.removeBorrowedBook(book.getTitle());

        //return confirmation
        String confirmation = "Return confirmed: " + book.getTitle();

        //acknowledge & return to functionality
        returnToFunctionalitySection();

        return confirmation;
    }

    public String logout() {
        //logout msg
        System.out.println("Logout successful. Returning to authentication");

        //clear session
        currentUser = null;

        //authentication prompt
        authenticationPrompt = true;
        promptCredentials();

        //confirmation
        return "Logout successful. Returning to authentication";
    }

    public boolean userAuthenticationPrompt() {
        return authenticationPrompt;
    }





}
