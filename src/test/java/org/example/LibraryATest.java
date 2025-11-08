package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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

        //User1 (alice) logs in and borrows "The Great Gatsby" - this is how it appears in initializeLibrary
        Borrower user1 = registry.findBorrowerUsername("alice");
        assertTrue(authSystem.validateCredentials(user1.getUsername(), user1.getPassword()));

        Book gatsby = catalogue.findBookByTitle("The Great Gatsby");
        String borrowConfirmation = authSystem.confirmBorrowing(gatsby, user1);
        assertTrue(borrowConfirmation.contains("Borrow confirmed"), "User1 should successfully borrow the book");

        //User1 logs out
        authSystem.logout();
        assertNull(authSystem.getCurrentUser(), "Current user should be null after logout");

        //User2(bob) logs in and sees the book as unavailable
        Borrower user2 = registry.findBorrowerUsername("bob");
        assertTrue(authSystem.validateCredentials(user2.getUsername(), user2.getPassword()));

        Book gatsbyForUser2 = catalogue.findBookByTitle("The Great Gatsby");
        assertEquals("Checked Out", gatsbyForUser2.getStatus(), "User2 should see 'The Great Gatsby' as checked out");

        //User1 logs back in and returns the book
        authSystem.validateCredentials(user1.getUsername(), user1.getPassword());
        String returnMsg = authSystem.returnBook(gatsby, user1, catalogue);
        assertTrue(returnMsg.contains("Return confirmed"), "User1 should successfully return the book");

        //User2 logs in again and sees the book as available
        authSystem.validateCredentials(user2.getUsername(), user2.getPassword());
        Book gatsbyAvailable = catalogue.findBookByTitle("The Great Gatsby");
        assertEquals("Available", gatsbyAvailable.getStatus(), "User2 should see 'The Great Gatsby' as available");
    }

    @Test
    @DisplayName("Initialization and Authentication with Error Handling")
    void A_TEST_02(){
        InitializeBorrowers initBorrowers = new InitializeBorrowers();
        BorrowerRegistry registry = initBorrowers.initializeBorrowers();
        InitializeLibrary initLibrary = new InitializeLibrary();
        Catalogue catalogue = initLibrary.initializeLibrary();
        Authenticator authSystem = new Authenticator(registry);

        //valid login
        Borrower validUser = registry.findBorrowerUsername("alice");
        boolean validLogin = authSystem.validateCredentials(validUser.getUsername(), validUser.getPassword());
        assertTrue(validLogin, "Valid login should succeed");

        //console output for menu
        ByteArrayOutputStream menuOut = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(menuOut));

        //check menu options displayed after login
        authSystem.displayAvailableOperations(validUser);

        System.setOut(originalOut);
        String menuOptions = menuOut.toString().trim();
        assertNotNull(menuOptions, "Menu options should be displayed after login");
        assertTrue(menuOptions.contains("Borrow") && menuOptions.contains("Return") && menuOptions.contains("Logout"),
                "Menu should include Borrow, Return, Logout options");

        //logout valid user
        authSystem.logout();
        assertNull(authSystem.getCurrentUser(), "User should be logged out successfully");

        //invalid login attempt
        Borrower invalidUser = new Borrower("FakeUser", "wrongPass");
        boolean invalidLogin = authSystem.validateCredentials(invalidUser.getUsername(), invalidUser.getPassword());
        assertFalse(invalidLogin, "Invalid login should be rejected");

        ByteArrayOutputStream promptOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(promptOut));

        //simulate retry prompt
        authSystem.promptCredentials();

        System.setOut(originalOut);
        String retryPrompt = promptOut.toString().trim();
        String expectedPrompt = "Enter username: Enter password:";
        assertEquals(expectedPrompt, retryPrompt.replaceAll("\\s+", " "), "System should prompt again after invalid login");
    }
}
