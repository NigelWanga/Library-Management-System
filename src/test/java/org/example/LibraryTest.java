package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LibraryTest {

    @Test
    @DisplayName("Check library catalogue size is 20")
    void RESP_01_test_01(){
        InitializeLibrary library = new InitializeLibrary();
        Catalogue catalogue = library.initializeLibrary();

        int size = catalogue.getCatalogueSize();

        assertEquals(20, size);


    }
    @Test
    @DisplayName("Check library catalogue for valid book - Great Gatsby.")
    void RESP_01_test_02(){
        InitializeLibrary library = new InitializeLibrary();
        Catalogue catalogue = library.initializeLibrary();

        Book book = catalogue.getBook(0);
        String title = book.getTitle();
        assertEquals("Great Gatsby",title);
    }

    @Test
    @DisplayName("Check borrower accounts size is 3")
    void RESP_02_test_01(){
        InitializeBorrowers borrowers = new InitializeBorrowers();
        BorrowerRegistry borrowerRegistry = borrowers.initializeBorrowers();

        int borrowSize = borrowerRegistry.getBorrowerSize();

        assertEquals(3, borrowSize);

    }

    @Test
    @DisplayName("Check initial books borrowed is 0")
    void RESP_02_test_02(){
        InitializeBorrowers borrowers = new InitializeBorrowers();
        BorrowerRegistry borrowerRegistry = borrowers.initializeBorrowers();


        for (Borrower borrower : borrowerRegistry.getAllBorrowers()){
            int borrowBookSize = borrower.getBorrowedBooksCount();
            assertEquals(0, borrowBookSize);
        }

    }

    @Test
    @DisplayName("Prompt borrower for authentication details")
    void RESP_03_test_01(){

        String usernamePrompt = null;
        String passwordPrompt = null;

        assertEquals("Enter username: ", usernamePrompt);
        assertEquals("Enter password: ", passwordPrompt);
    }

    @Test
    @DisplayName("Permit borrower to input username and password")
    void RESP_03_test_02(){

        String usernamePermit = null;
        String passwordPermit = null;

        assertEquals("user1: ", usernamePermit);
        assertEquals("pass1: ", passwordPermit);
    }


    @Test
    @DisplayName("Check borrower username and password validity")
    void RESP_03_test_03(){

        String validUsername = "user1";
        String validPassword = "pass1";

        String enteredUsername = null;
        String enteredPassword = null;

        assertEquals(validUsername, enteredUsername);
        assertEquals(validPassword, enteredPassword);

    }


}
