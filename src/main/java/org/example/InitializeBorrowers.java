package org.example;

public class InitializeBorrowers {

    BorrowerRegistry borrowerRegistry = new BorrowerRegistry();
    //baseline
    public BorrowerRegistry initializeBorrowers() {
        borrowerRegistry.addBorrower(new Borrower("alice", "pass123"));
        borrowerRegistry.addBorrower(new Borrower("bob", "pass456"));
        borrowerRegistry.addBorrower(new Borrower("charlie", "pass789"));
        return borrowerRegistry;
    }
}
