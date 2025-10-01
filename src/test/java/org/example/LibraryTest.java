package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LibraryTest {

    @Test
    @DisplayName("Check library catalogue size is 20")
    void RESP_01_test_01(){
        Library library = new Library();
        library.initializeLibrary();
        assertEquals(20, library.getCatalogueSize());
    }

    @Test
    @DisplayName("Check library catalogue for valid book - Great Gatsby.")
    void RESP_02_test_02(){
        Library library = new Library();
        library.initializeLibrary();
        Book book = library.getBook(0);
        assertEquals("Great Gatsby", book.getTitle());
    }


}
