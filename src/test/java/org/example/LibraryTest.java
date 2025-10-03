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
        BorrowerRegistry borrowerRegistry = new BorrowerRegistry();
        borrowerRegistry.initializeBorrowers();

        int borrowSize = borrowerRegistry.getBorrowerSize();

        assertEquals(3, borrowSize);

    }

    @Test
    @DisplayName("Check initial books borrowed is 0")
    void RESP_02_test_02(){
        BorrowerRegistry borrowerRegistry = new BorrowerRegistry();
        borrowerRegistry.initializeBorrowers();


        for (Borrower borrower : borrowerRegistry.getAllBorrowers()){
            int borrowBookSize = borrower.getBorrowedBooksSize();
            assertEquals(0, borrowBookSize);
        }

    }
}
