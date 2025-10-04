package org.example;

import java.util.Scanner;

public class Authenticator {

    private BorrowerRegistry borrowerRegistry;

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
}
