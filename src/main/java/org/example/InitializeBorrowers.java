package org.example;

public class InitializeBorrowers {

    BorrowerRegistry borrowerRegistry = new BorrowerRegistry();

    public BorrowerRegistry initializeBorrowers() {
        borrowerRegistry.addBorrower(new Borrower("Spel", "123"));
        borrowerRegistry.addBorrower(new Borrower("Nord", "456"));
        borrowerRegistry.addBorrower(new Borrower("Aeil", "789"));
        return borrowerRegistry;
    }
}
