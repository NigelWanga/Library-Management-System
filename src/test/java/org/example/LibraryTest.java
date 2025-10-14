package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

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
        assertEquals(1, count, "Borrower should've borrowed 1 book");

    }

    @Test
    @DisplayName("Check for display of books in collection")
    void RESP_09_test_01(){
        TestSetup setup = new TestSetup("Nord", "456");
        Catalogue catalogue = setup.getCatalogue();

        //display all books
        ArrayList<Book> books = catalogue.getAllBooks();
        assertNotNull(books, "Books list should not be null");

        for (Book book : books) {
            assertNotNull(book.getTitle(), "Book title should not be null");
            assertNotNull(book.getAuthor(), "Book author should not be null");
            assertNotNull(book.getStatus(), "Book should have status");

            //if checked out, display due date
            if (book.getStatus().equals("Checked out")) {
                assertNotNull(book.getDueDate(), "Checked out books have due date");
            }
        }
    }

    @Test
    @DisplayName("Check for available book selection")
    void RESP_10_test_01(){
        TestSetup setup = new TestSetup("Nord", "456");
        Catalogue catalogue = setup.getCatalogue();
        Authenticator authSystem = setup.getAuthSystem();


        //borrower selects book
        Book selectedBook = authSystem.selectAvailableBook(catalogue);
        assertNotNull(selectedBook, "Available book is selected");
    }


    @Test
    @DisplayName("Check for presentation of selected book and borrowing details")
    void RESP_10_test_02(){
        TestSetup setup = new TestSetup("Nord", "456");
        Catalogue catalogue = setup.getCatalogue();
        Authenticator authSystem = setup.getAuthSystem();

        Book selectedBook = authSystem.selectAvailableBook(catalogue);

        //display book details
        String details = authSystem.presentBorrowingDetails(selectedBook);

        assertTrue(details.contains("Title:"), "Details should include title");
        assertTrue(details.contains("Author:"), "Details should include author");
    }



    @Test
    @DisplayName("Check for borrower confirmation")
    void RESP_10_test_03(){
        TestSetup setup = new TestSetup("Nord", "456");
        Catalogue catalogue = setup.getCatalogue();
        Borrower currentUser = setup.getCurrentUser();
        Authenticator authSystem = setup.getAuthSystem();

        Book selectedBook = authSystem.selectAvailableBook(catalogue);

        //confirm transaction
        String confirmation = authSystem.confirmBorrowing(selectedBook, currentUser);

        assertTrue(confirmation.contains("Borrow confirmed"), "Borrowing should be confirmed");
        assertTrue(currentUser.getBorrowedBooks().contains(selectedBook.getTitle()), "Book recorded as borrowed");

    }



    @Test
    @DisplayName("Check system verification on book availability")
    void RESP_11_test_01(){
        TestSetup setup = new TestSetup("Nord", "456");
        Catalogue catalogue = setup.getCatalogue();
        Borrower currentUser = setup.getCurrentUser();
        Authenticator authSystem = setup.getAuthSystem();

        //select book
        Book selectedBook = authSystem.selectAvailableBook(catalogue);
        assertNotNull(selectedBook, "Available book is selected");

        //verify availability of book
        boolean isAvailable = authSystem.verifyBookAvailability(selectedBook, currentUser);
        assertTrue(isAvailable, "Book available for borrowing");

        //borrow book
        String confirmation = authSystem.confirmBorrowing(selectedBook, currentUser);
        assertTrue(confirmation.contains("Borrow confirmed"), "Borrowing should be confirmed");

        //verify book is checked out
        assertEquals("Checked out", selectedBook.getStatus(), "Book status must be 'Checked out'");

        //so if we attempt to borrow same book, it fails
        boolean secondCheck = authSystem.verifyBookAvailability(selectedBook, currentUser);
        assertFalse(secondCheck, "Book shouldn't be available after borrowing");

    }



    @Test
    @DisplayName("Check for verification eligibility to borrow book")
    void RESP_12_test_01(){
        TestSetup setup = new TestSetup("Nord", "456");
        Borrower currentUser = setup.getCurrentUser();
        Authenticator authSystem = setup.getAuthSystem();


        //borrower has borrowed 3 books
        currentUser.addBorrowedBook("Treasure Island");
        currentUser.addBorrowedBook("Great Expectations");
        currentUser.addBorrowedBook("The Return of the King");

        boolean isEligible = authSystem.verifyBorrowerEligibility(currentUser);

        assertFalse(isEligible, "Borrower shouldn't be eligible to borrow more than 3 books");

    }



    @Test
    @DisplayName("Check due date calculation")
    void RESP_13_test_01(){
        TestSetup setup = new TestSetup("Nord", "456");
        Borrower currentUser = setup.getCurrentUser();
        Catalogue catalogue = setup.getCatalogue();

        //select first book and borrow
        Book selectedBook = catalogue.getAllBooks().get(0);
        selectedBook.borrowBook();
        currentUser.addBorrowedBook(selectedBook.getTitle());


        //due date should be 14 days from current date
        LocalDate expectedDueDate = LocalDate.now().plusDays(14);
        LocalDate actualDueDate = selectedBook.getDueDate();

        //fails, since due date should be 14 days
        assertEquals(expectedDueDate, actualDueDate, "Due date should be 14 days");

    }


    @Test
    @DisplayName("Check recording of borrowing transaction and borrower information")
    void RESP_14_test_01(){
        TestSetup setup = new TestSetup("Nord", "456");
        Borrower currentUser = setup.getCurrentUser();
        Catalogue catalogue = setup.getCatalogue();
        Authenticator authSystem = setup.getAuthSystem();

        //select first available book
        Book bookToBorrow = authSystem.selectAvailableBook(catalogue);

        //confirm borrowing transaction
        String confirmation = authSystem.confirmBorrowing(bookToBorrow, currentUser);

        assertNotNull(bookToBorrow, "Book to borrow should not be null");
        assertTrue(currentUser.getBorrowedBooks().contains(bookToBorrow.getTitle()),"Borrower should have the book recorded");
        assertEquals("Checked out", bookToBorrow.getStatus(),"Book status should be 'Checked out'");
        assertNotNull(bookToBorrow.getDueDate(),"Borrowed book should have a due date");
        assertTrue(confirmation.contains("Borrow confirmed"),"Confirmation message should show borrow success");

    }


    @Test
    @DisplayName("Check borrower account update & book availability status")
    void RESP_15_test_01(){
        TestSetup setup = new TestSetup("Nord", "456");
        Borrower currentUser = setup.getCurrentUser();
        Catalogue catalogue = setup.getCatalogue();
        Authenticator authSystem = setup.getAuthSystem();

        //select book
        Book selectedBook = authSystem.selectAvailableBook(catalogue);

        //update borrower and book
        authSystem.updateBorrowerAndBook(currentUser, selectedBook);

        //skip with updating borrower & book
        assertTrue(currentUser.getBorrowedBooks().contains(selectedBook.getTitle()), "Borrower should now have the book recorded");
        assertEquals("Checked out", selectedBook.getStatus(), "Book status must be 'Checked out'");
    }



    @Test
    @DisplayName("Check borrowing confirmation and borrower acknowledgment")
    void RESP_16_test_01(){
        TestSetup setup = new TestSetup("Nord", "456");
        Borrower currentUser = setup.getCurrentUser();
        Catalogue catalogue = setup.getCatalogue();
        Authenticator authSystem = setup.getAuthSystem();

        //select book
        Book selectedBook = authSystem.selectAvailableBook(catalogue);
        assertNotNull(selectedBook, "Available book should be selected");

        //confirm borrowing
        String confirmation = authSystem.confirmBorrowing(selectedBook, currentUser);

        //check wrong confirmation - fails
        assertTrue(confirmation.contains("Borrow confirmed"), "Borrowing should be confirmed");

        //borrower does not acknowledge
        boolean acknowledge = authSystem.acknowledgeBorrowing(selectedBook, currentUser);
        assertTrue(acknowledge, "Borrower acknowledges completion");

    }

    @Test
    @DisplayName("Check system returns to available functionality after borrowing")
    void RESP_16_test_02(){
        TestSetup setup = new TestSetup("Nord", "456");
        Authenticator authSystem = setup.getAuthSystem();

        boolean returnToFunctionality = authSystem.returnToFunctionalitySection();
        assertTrue(returnToFunctionality, "System should return to available functionality");

    }



    @Test
    @DisplayName("Check system handling of unavailable book and hold placement")
    void RESP_17_test_01(){
        TestSetup setup = new TestSetup("Nord", "456");
        Borrower currentUser = setup.getCurrentUser();
        Catalogue catalogue = setup.getCatalogue();
        Authenticator authSystem = setup.getAuthSystem();

        //borrower borrows book
        Book borrowedBook = catalogue.getBookHeld("Treasure Island");
        borrowedBook.borrowBook();
        String response = authSystem.handleUnavailableBook(borrowedBook, currentUser);

        assertTrue(response.contains("Hold"), "System should handle hold placement properly for unavailable book");

    }

    @Test
    @DisplayName("Check borrower - has borrowed max of 3 books")
    void RESP_18_test_01(){
        TestSetup setup = new TestSetup("Nord", "456");
        Borrower currentUser = setup.getCurrentUser();
        Catalogue catalogue = setup.getCatalogue();
        Authenticator authSystem = setup.getAuthSystem();

        // Borrow 3 books first
        Book book1 = authSystem.selectAvailableBook(catalogue);
        authSystem.updateBorrowerAndBook(currentUser, book1);

        Book book2 = authSystem.selectAvailableBook(catalogue);
        authSystem.updateBorrowerAndBook(currentUser, book2);

        Book book3 = authSystem.selectAvailableBook(catalogue);
        authSystem.updateBorrowerAndBook(currentUser, book3);

        // Attempt to borrow 4th book
        Book book4 = authSystem.selectAvailableBook(catalogue);
        String response = authSystem.confirmBorrowing(book4, currentUser);

        // Assertions
        assertTrue(response.contains("Maximum borrowing limit reached"), "System should notify max borrowing limit reached");
        assertEquals(3, currentUser.getBorrowedBooks().size(), "Borrower should still have only 3 books");

    }



    @Test
    @DisplayName("Check that system displays borrower's current borrowed books w/ due dates")
    void RESP_19_test_01(){
        TestSetup setup = new TestSetup("Aeil", "789");
        Borrower currentUser = setup.getCurrentUser();
        Catalogue catalogue = setup.getCatalogue();
        Authenticator authSystem = setup.getAuthSystem();

        Book borrowedBook1 = authSystem.selectAvailableBook(catalogue);
        authSystem.updateBorrowerAndBook(currentUser, borrowedBook1);

        Book borrowedBook2 = authSystem.selectAvailableBook(catalogue);
        authSystem.updateBorrowerAndBook(currentUser, borrowedBook2);

        //display borrowed books
        String borrowedBooksDisplay = authSystem.displayBorrowedBooks(currentUser, catalogue);

        //check that each borrowed book title and due date is included
        assertTrue(borrowedBooksDisplay.contains(borrowedBook2.getTitle()), "Display should include first borrowed book title");
        assertTrue(borrowedBooksDisplay.contains(borrowedBook1.getDueDate().toString()), "Display should include first borrowed book due date");

        assertTrue(borrowedBooksDisplay.contains(borrowedBook1.getTitle()), "Display should include second borrowed book title");
        assertTrue(borrowedBooksDisplay.contains(borrowedBook2.getDueDate().toString()), "Display should include second borrowed book due date");
        
    }
    
    
    
    @Test
    @DisplayName("Check for borrower book return process ")
    void RESP_20_test_01(){
        TestSetup setup = new TestSetup("Aeil", "789");
        Borrower currentUser = setup.getCurrentUser();
        Catalogue catalogue = setup.getCatalogue();
        Authenticator authSystem = setup.getAuthSystem();

        //borrow book
        Book borrowBook = authSystem.selectAvailableBook(catalogue);
        authSystem.updateBorrowerAndBook(currentUser, borrowBook);

        //returning borrowed book
        String returnBookMsg = authSystem.returnBook(borrowBook, currentUser, catalogue);

        assertEquals("Return confirmed: " + borrowBook.getTitle(), returnBookMsg, "System confirms return");
        assertTrue(currentUser.getBorrowedBooks().isEmpty(), "Borrower's book list should be empty");
        assertEquals("Available", borrowBook.getStatus(), "Book status should be available");
        assertTrue(authSystem.returnToFunctionalitySection(), "System returns to available functionality");
    }





    @Test
    @DisplayName("Manage borrower who has no books currently borrowed")
    void RESP_21_test_01(){
        TestSetup setup = new TestSetup("Aeil", "789");
        Borrower currentUser = setup.getCurrentUser();
        Catalogue catalogue = setup.getCatalogue();
        Authenticator authSystem = setup.getAuthSystem();


        Book book = authSystem.selectAvailableBook(catalogue);
        String result = authSystem.returnBook(book, currentUser, catalogue);

        assertEquals("No books are currently borrowed", result, "Notify borrower that no books currently borrowed");

    }



}
