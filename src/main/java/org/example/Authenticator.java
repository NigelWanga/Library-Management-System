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


    public void handleInvalidLogin(String username, String password) {
        boolean isValid = validateCredentials(username, password);

        if (!isValid) {
            System.out.println("Authentication failed: Invalid username or password");
            System.out.println("Please try again");
            return;
        }

        System.out.println("Authentication successful");
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

    public Borrower loginPrompt(Scanner input) {
        boolean authenticated = false;
        Borrower borrower = null;

        while (!authenticated) {
            System.out.print("Enter username: ");
            String username = input.nextLine();

            System.out.print("Enter password: ");
            String password = input.nextLine();

            if (validateCredentials(username, password)) {
                borrower = borrowerRegistry.findBorrowerUsername(username);
                authenticated = true;
                System.out.println("Authentication successful! Welcome, " + borrower.getUsername() + ".");
            } else {
                System.out.println("Invalid credentials. Please try again.\n");
            }
        }

        return borrower;
    }


    public void checkAvailableHolds(Borrower borrower, Catalogue catalogue) {
        for (String heldTitle : borrower.getHeldBooks()) {
            Book heldBook = catalogue.getBookHeld(heldTitle);
            if (heldBook != null && heldBook.isAvailable()) {
                System.out.println("Book available: " + heldBook.getTitle() + " by " + heldBook.getAuthor());
            }
        }
    }


    public void displayAvailableOperations(Borrower borrower) {
        System.out.println("Available operations: Borrow | Return | Logout");
    }


    public String displayBookStatus(Book book, Borrower borrower) {
        if (book.isOnHold() && !borrower.getHeldBooks().contains(book.getTitle())) {
            return "On Hold"; //another borrower has it
        }
        return book.getStatus();
    }

    public void setCurrentUser(Borrower borrower) { this.currentUser = borrower; }
    public Borrower getCurrentUser() { return currentUser; }


    public Book selectAvailableBook(Catalogue catalogue) {
        for (Book book : catalogue.getAllBooks()) {
            if (book.isAvailable()) {
                return book; //this is when borrower selects first available book
            }
        }
        return null;
    }

    public String presentBorrowingDetails(Book selectedBook) { //changed - resp_10
        if (selectedBook == null) return "No book selected";

        return "Selected Book Details:\n" +
                "Title: " + selectedBook.getTitle() + "\n" +
                "Author: " + selectedBook.getAuthor() + "\n" +
                "Status: " + selectedBook.getStatus();
    }

    public String confirmBorrowing(Book selectedBook, Borrower borrower) { //changed - resp_10

        if (selectedBook == null) { return "No book selected for confirmation."; }
        if (!selectedBook.isAvailable()) { return "Book is not available."; }

        //prevent duplicate borrowing
        if (borrower.getBorrowedBooks().contains(selectedBook.getTitle())) { return "You have already borrowed this book."; }

        //max borrowing limit
        if (borrower.getBorrowedBooks().size() >= 3) { return "Maximum borrowing limit reached."; }

        // Update both book and borrower records
        selectedBook.borrowBook();
        borrower.addBorrowedBook(selectedBook.getTitle());

        return "Borrow confirmed: " + selectedBook.getTitle() +
                "\nDue Date: " + selectedBook.getDueDate();
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

    public void acknowledgeBorrowing(Book book, Borrower borrower) {} //changed - resp_16
    public void returnToFunctionalitySection() {} //changed - resp_16

    public String handleUnavailableBook(Book book, Borrower borrower) { //changed - resp_17
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


    public String displayBorrowedBooks(Borrower borrower, Catalogue catalogue) { //changed - resp_19
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

    public String returnBook(Book book, Borrower borrower, Catalogue catalogue) { //changed - resp_20
        if (book == null) { return "No book selected for return."; }
        if (borrower == null) { return "No borrower currently logged in."; }

        //check if borrower actually borrowed the book
        if (!borrower.getBorrowedBooks().contains(book.getTitle())) { return "Cannot return: book not borrowed or does not exist."; }

        //handle if borrower list empty
        if (borrower.getBorrowedBooks().isEmpty()) { return "No books are currently borrowed."; }

        String message;

        //for multiple holds queue processing
        if (book.hasQueuedHolds()) {
            String nextUser = book.popHold();
            book.setBorrowed(false);
            book.setOnHold(true);
            book.onHoldBy = nextUser;
            book.setStatus("On Hold");
            borrower.getBorrowedBooks().remove(book.getTitle());
            return nextUser + " should be notified that " + book.getTitle() + " is now available";
        }

        //if no hold
        if (book.getHoldBy() == null || book.getHoldBy().isEmpty()) {
            book.returnBook(); //resets borrowed, onHold, dueDate
            borrower.getBorrowedBooks().remove(book.getTitle());
            message = "Return confirmed: " + book.getTitle() + " (no holds pending)";
        } else {
            String nextUser = book.getHoldBy();
            book.setBorrowed(false);
            book.setOnHold(true);
            book.setStatus("On Hold");
            borrower.getBorrowedBooks().remove(book.getTitle());
            message = "Return confirmed: " + book.getTitle() + " is now On Hold for " + nextUser;
        }
        return message;
    }

    public String logout() {
        //logout msg
        //System.out.println("Logout successful. Returning to authentication");

        //clear session
        currentUser = null;

        //authentication prompt
        authenticationPrompt = true;
        //promptCredentials();

        //confirmation
        return "Logout successful. Returning to authentication";
    }

}
