package org.example;

import java.util.ArrayList;

public class BorrowerRegistry {
    ArrayList<Borrower> borrowers;

    public BorrowerRegistry() {borrowers = new ArrayList<Borrower>();}
    public void addBorrower(Borrower borrower) {borrowers.add(borrower); }
    public int getBorrowerSize() {return borrowers.size(); }
    public ArrayList<Borrower> getAllBorrowers() { return borrowers; }

    public Borrower findBorrowerUsername(String username) {
        for (Borrower b : borrowers) {
            if (b.getUsername().equals(username)) {
                return b;
            }
        }
        return null;
    }

}
