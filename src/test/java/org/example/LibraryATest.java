package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class LibraryATest {

    @Test
    @DisplayName("Multi-User Borrow and Return with Availability Validated")
    void A_TEST_01(){
        //initialize borrowers and library
        InitializeBorrowers initBorrowers = new InitializeBorrowers();
        BorrowerRegistry registry = initBorrowers.initializeBorrowers();
        InitializeLibrary initLibrary = new InitializeLibrary();
        Catalogue catalogue = initLibrary.initializeLibrary();

        Authenticator authSystem = new Authenticator(registry);

        //User1 (Spel) logs in and borrows "Great Gatsby" - this is how it appears in initializeLibrary
        Borrower user1 = registry.findBorrowerUsername("Spel");
        assertTrue(authSystem.validateCredentials(user1.getUsername(), user1.getPassword()));

        Book gatsby = catalogue.findBookByTitle("Great Gatsby");
        String borrowConfirmation = authSystem.confirmBorrowing(gatsby, user1);
        assertTrue(borrowConfirmation.contains("Borrow confirmed"), "User1 should successfully borrow the book");

        //User1 logs out
        authSystem.logout();
        assertNull(authSystem.getCurrentUser(), "Current user should be null after logout");

        //User2(Nord) logs in and sees the book as unavailable
        Borrower user2 = registry.findBorrowerUsername("Nord");
        assertTrue(authSystem.validateCredentials(user2.getUsername(), user2.getPassword()));

        Book gatsbyForUser2 = catalogue.findBookByTitle("Great Gatsby");
        assertEquals("Checked Out", gatsbyForUser2.getStatus(), "User2 should see 'Great Gatsby' as checked out");

        //User1 logs back in and returns the book
        authSystem.validateCredentials(user1.getUsername(), user1.getPassword());
        String returnMsg = authSystem.returnBook(gatsby, user1, catalogue);
        assertTrue(returnMsg.contains("Return confirmed"), "User1 should successfully return the book");

        //User2 logs in again and sees the book as available
        authSystem.validateCredentials(user2.getUsername(), user2.getPassword());
        Book gatsbyAvailable = catalogue.findBookByTitle("Great Gatsby");
        assertEquals("Available", gatsbyAvailable.getStatus(), "User2 should see 'Great Gatsby' as available");
    }

}
