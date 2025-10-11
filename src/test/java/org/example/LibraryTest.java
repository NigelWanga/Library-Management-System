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
        InitializeBorrowers initborrowers = new InitializeBorrowers();
        BorrowerRegistry registry = initborrowers.initializeBorrowers();
        Authenticator authSystem = new Authenticator(registry);

        String[] prompts = authSystem.promptCredentials();

        //expected prompts
        assertEquals("Enter username: ", prompts[0]);
        assertEquals("Enter password: ", prompts[1]);
    }

    @Test
    @DisplayName("Permit borrower to input username and password")
    void RESP_03_test_02(){
        InitializeBorrowers initborrowers = new InitializeBorrowers();
        BorrowerRegistry registry = initborrowers.initializeBorrowers();
        Authenticator authSystem = new Authenticator(registry);

        //input simulation
        String username = "Spel";
        String password = "123";

        String[] inputs = authSystem.captureCredentials(username, password);

        assertEquals("Spel", inputs[0]);
        assertEquals("123", inputs[1]);
    }


    @Test
    @DisplayName("Check borrower username and password validity")
    void RESP_03_test_03(){
        InitializeBorrowers initborrowers = new InitializeBorrowers();
        BorrowerRegistry registry = initborrowers.initializeBorrowers();
        Authenticator authSystem = new Authenticator(registry);

        boolean isValid = authSystem.validateCredentials("Spel", "123");
        boolean isInValid = authSystem.validateCredentials("invalidUser", "invalidPassword");

        assertTrue(isValid);
        assertFalse(isInValid);
    }

    @Test
    @DisplayName("Set authenticated borrower as current user - active session")
    void RESP_04_test_01(){
        InitializeBorrowers initborrowers = new InitializeBorrowers();
        BorrowerRegistry registry = initborrowers.initializeBorrowers();
        Authenticator authSystem = new Authenticator(registry);

        //log in with correct cred
        boolean isLoggedIn = authSystem.login("Spel", "123");
        Borrower currentUser = authSystem.getCurrentUser();

        //establishing session & successful login
        assertNotNull(currentUser);
        assertTrue(isLoggedIn);

    }


    @Test
    @DisplayName("Check if borrower holds have become available & display noti & book info")
    void RESP_05_test_01(){
        TestSetup setup = new TestSetup("Spel", "123");
        Catalogue catalogue = setup.getCatalogue();
        Borrower currentUser = setup.getCurrentUser();
        Authenticator authSystem = setup.getAuthSystem();

        //here we assume an arbitrary title/book
        currentUser.getHeldBooks().add("War and Peace"); //hold
        Book warAndPeace = catalogue.getBookHeld("War and Peace");
        warAndPeace.setBorrowed(false); //available

        String notification = authSystem.checkAvailableHolds(currentUser, catalogue);

        assertNotNull(currentUser, "Current user is active" );
        assertNotNull(notification, "Notify borrower of available held books");
        System.out.println(notification);

    }

    @Test
    @DisplayName("Check if operations presented - borrow, return, logout")
    void RESP_06_test_01(){
        TestSetup setup = new TestSetup("Spel", "123");
        Catalogue catalogue = setup.getCatalogue();
        Borrower currentUser = setup.getCurrentUser();
        Authenticator authSystem = setup.getAuthSystem();

        //prompt currentUSer/active user to either (borrow, return or logout)
        String displayOperations = authSystem.displayAvailableOperations(currentUser);

        //borrow
            //present to the user the books available to borrow (those on hold too?)
        //return
            //present to the user the current books they have, and which of them they want to return
        //logout
            //make the current user inactive, basically, log them out (perhaps isLoggedIn plays a part in this

        assertNotNull(currentUser, "Current user is active" );
        assertNotNull(displayOperations, "Display available operations to user");
        assertTrue(displayOperations.contains("Borrow"), "Include borrow operation");
        assertTrue(displayOperations.contains("Return"),  "Include return operation");
        assertTrue(displayOperations.contains("Logout"),   "Include logout operation");

    }

    @Test
    @DisplayName("Check for invalid credentials - authentication error")
    void RESP_07_test_01(){
        InitializeBorrowers initborrowers = new InitializeBorrowers();
        BorrowerRegistry registry = initborrowers.initializeBorrowers();
        Authenticator authSystem = new Authenticator(registry);

        boolean result = authSystem.handleInvalidLogin("invalidUser", "invalidPassword");
        assertFalse(result, "Expect false for invalid credentials");

    }

    @Test
    @DisplayName("Check for wrong authentication prompt text")
    void RESP_07_test_02(){
        InitializeBorrowers initborrowers = new InitializeBorrowers();
        BorrowerRegistry registry = initborrowers.initializeBorrowers();
        Authenticator authSystem = new Authenticator(registry);

        String[] prompts = authSystem.promptCredentials();

        //expected prompts
        assertEquals("Enter username: ", prompts[0]);
        assertEquals("Enter password: ", prompts[1]);

    }

    @Test
    @DisplayName("Display borrower current book count")
    void RESP_08_test_01(){
        TestSetup setup = new TestSetup("Nord", "456");
        Borrower currentUser = setup.getCurrentUser();

        //attribute 1 borrowed book to Nord
        currentUser.getBorrowedBooks().add("War and Peace");

        int count = currentUser.getBorrowedBooksCount();

        //fail, since Nord has 1 book
        assertEquals(2, count, "Expect count of borrowed Books - error");


    }



}
