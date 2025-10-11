package org.example;

import java.util.Scanner;

public class Authenticator {

    private BorrowerRegistry borrowerRegistry;
    private Borrower currentUser;

    public Authenticator(BorrowerRegistry borrowerRegistry) {
        this.borrowerRegistry = borrowerRegistry;
    }

    //prompt for credentials
    public String[] promptCredentials() {
        String usernamePrompt = "Enter username: ";
        String passwordPrompt = "Enter password: ";
        System.out.print(usernamePrompt);
        System.out.print(passwordPrompt);

        return new String[]{usernamePrompt, passwordPrompt};
    }

    //Capture borrower credentials
    public String[] captureCredentials(String username, String password) {
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

    public Borrower getCurrentUser() { return currentUser; }
}
