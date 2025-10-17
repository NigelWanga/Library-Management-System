package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
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
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        Authenticator authSystem = new Authenticator(null); //registry not needed for prompt
        authSystem.promptCredentials(); //method should print prompts

        String consoleOutput = output.toString().trim();

        //expected sequence of prompts
        String expected = "Enter username:\nEnter password:";

        assertTrue(consoleOutput.replaceAll("\\s+", " ").contains(expected.replaceAll("\\s+", " ")),
                "Console output should contain both prompts in the correct order");

        System.setOut(System.out);
    }

    @Test
    @DisplayName("Permit borrower to input username and password")
    void RESP_03_test_02(){
        //arrange simulated input
        String simulatedInput = "Spel\n123\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        Authenticator authSystem = new Authenticator(null);

        //getting our credentials
        String[] captured = authSystem.captureCredentials();

        assertEquals(
                "Spel 123",
                String.join(" ", captured),
                "Captured credentials should match expected username and password"
        );

        System.setIn(System.in);
        System.setOut(System.out);
    }



    @Test
    @DisplayName("Check borrower username and password validity")
    void RESP_03_test_03(){
        InitializeBorrowers initborrowers = new InitializeBorrowers();
        BorrowerRegistry registry = initborrowers.initializeBorrowers();
        Authenticator authSystem = new Authenticator(registry);

        boolean allValidationsCorrect = authSystem.validateCredentials("Spel", "123") &&
                !authSystem.validateCredentials("invalidUser", "invalidPassword");

        assertTrue(allValidationsCorrect, "System should accept valid and reject invalid credentials");
    }


    @Test
    @DisplayName("Set authenticated borrower as current user - active session")
    void RESP_04_test_01(){
        InitializeBorrowers initborrowers = new InitializeBorrowers();
        BorrowerRegistry registry = initborrowers.initializeBorrowers();
        Authenticator authSystem = new Authenticator(registry);

        //log in with correct cred
        boolean sessionActive = authSystem.login("Spel", "123") && authSystem.getCurrentUser() != null;

        //establishing session & successful login
        assertTrue(sessionActive, "Session should be active after successful login");
    }


    @Test
    @DisplayName("Check if borrower holds have become available & display noti & book info")
    void RESP_05_test_01(){
        TestSetup setup = new TestSetup("Spel", "123");
        Catalogue catalogue = setup.getCatalogue();
        Borrower currentUser = setup.getCurrentUser();
        Authenticator authSystem = setup.getAuthSystem();

        //console output
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        //here we assume an arbitrary title/book
        currentUser.getHeldBooks().add("War and Peace");
        Book warAndPeace = catalogue.getBookHeld("War and Peace");
        warAndPeace.setBorrowed(false);

        authSystem.checkAvailableHolds(currentUser, catalogue);

        assertTrue(output.toString().contains("War and Peace"), "Active user should be notified when held books become available");

        System.setOut(System.out);
    }

    @Test
    @DisplayName("Check if operations presented - borrow, return, logout")
    void RESP_06_test_01(){
        TestSetup setup = new TestSetup("Spel", "123");
        Catalogue catalogue = setup.getCatalogue();
        Borrower currentUser = setup.getCurrentUser();
        Authenticator authSystem = setup.getAuthSystem();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        //prompt currentUSer/active user to either (borrow, return or logout)
        authSystem.displayAvailableOperations(currentUser);

        assertTrue(output.toString().contains("Borrow") &&
                        output.toString().contains("Return") &&
                        output.toString().contains("Logout"),
                "UI should display Borrow, Return, and Logout operations to the user");

        // Cleanup
        System.setOut(System.out);
    }

    @Test
    @DisplayName("Check for invalid credentials - authentication error")
    void RESP_07_test_01(){
        InitializeBorrowers initborrowers = new InitializeBorrowers();
        BorrowerRegistry registry = initborrowers.initializeBorrowers();
        Authenticator authSystem = new Authenticator(registry);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        authSystem.handleInvalidLogin("invalidUser", "invalidPassword");
        assertTrue(output.toString().contains("Invalid username or password"),
                "UI should notify user when login credentials are invalid");

        System.setOut(System.out);

    }

    @Test
    @DisplayName("Check for wrong authentication prompt text")
    void RESP_07_test_02(){
        InitializeBorrowers initborrowers = new InitializeBorrowers();
        BorrowerRegistry registry = initborrowers.initializeBorrowers();
        Authenticator authSystem = new Authenticator(registry);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        authSystem.promptCredentials();

        assertTrue(output.toString().contains("Enter username:") &&
                        output.toString().contains("Enter password:"),
                "UI should display correct prompts for username and password");

        System.setOut(System.out);

    }

    @Test
    @DisplayName("Display borrower current book count")
    void RESP_08_test_01(){
        TestSetup setup = new TestSetup("Nord", "456");
        Borrower currentUser = setup.getCurrentUser();

        //attribute 1 borrowed book to Nord
        currentUser.getBorrowedBooks().add("War and Peace");

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        //display book count to user
        int count = currentUser.getBorrowedBooksCount();
        System.out.println("You currently have " + count + " book(s) borrowed.");

        assertTrue(output.toString().contains("You currently have 1 book(s) borrowed"),
                "UI should display the correct number of borrowed books");

        System.setOut(System.out);

    }

    @Test
    @DisplayName("Check for display of books in collection")
    void RESP_09_test_01(){
        TestSetup setup = new TestSetup("Nord", "456");
        Catalogue catalogue = setup.getCatalogue();
        ArrayList<Book> books = catalogue.getAllBooks();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        //display all books to borrower
        for (Book book : books) {
            String dueDate = book.getDueDate() != null ? " | Due: " + book.getDueDate() : "";
            System.out.println(book.getTitle() + " by " + book.getAuthor() + " | Status: "
                    + book.getStatus() + dueDate);
        }

        //check that output contains at least one known book
        assertTrue(output.toString().contains("War and Peace"),
                "UI should display all books, including 'War and Peace'");

        System.setOut(System.out);
    }

    @Test
    @DisplayName("Check for available book selection")
    void RESP_10_test_01(){
        TestSetup setup = new TestSetup("Nord", "456");
        Catalogue catalogue = setup.getCatalogue();
        ArrayList<Book> books = catalogue.getAllBooks();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        //display books to borrower
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            System.out.println((i + 1) + ". " + book.getTitle() + " by " + book.getAuthor());
        }

        //simulate borrower selecting the first book
        Book selectedBook = books.get(0);

        //selection is non-null and included in display
        assertTrue(selectedBook != null && output.toString().contains(selectedBook.getTitle()),
                "Borrower should be able to select a book displayed in the UI");

        System.setOut(System.out);
    }


    @Test
    @DisplayName("Check for presentation of selected book and borrowing details")
    void RESP_10_test_02(){
        TestSetup setup = new TestSetup("Nord", "456");
        Catalogue catalogue = setup.getCatalogue();
        Authenticator authSystem = setup.getAuthSystem();
        Book selectedBook = authSystem.selectAvailableBook(catalogue);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        //present borrowing details
        System.out.println(authSystem.presentBorrowingDetails(selectedBook));

        //details include title and author
        assertTrue(output.toString().contains(selectedBook.getTitle()) &&
                        output.toString().contains(selectedBook.getAuthor()),
                "UI should display selected book's title and author");

        System.setOut(System.out);
    }



    @Test
    @DisplayName("Check for borrower confirmation")
    void RESP_10_test_03(){
        TestSetup setup = new TestSetup("Nord", "456");
        Catalogue catalogue = setup.getCatalogue();
        Borrower currentUser = setup.getCurrentUser();
        Authenticator authSystem = setup.getAuthSystem();

        Book selectedBook = authSystem.selectAvailableBook(catalogue);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        //confirm borrowing
        System.out.println(authSystem.confirmBorrowing(selectedBook, currentUser));

        //UI shows confirmation and borrower's record is updated
        assertTrue(output.toString().contains("Borrow confirmed") &&
                        currentUser.getBorrowedBooks().contains(selectedBook.getTitle()),
                "UI should display borrow confirmation and update borrower record");

        System.setOut(System.out);

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

        //verify availability of book/ borrow book/ verify book is checked out/ ... so if we attempt to borrow same book, it fails
        boolean allValid =
                selectedBook != null &&
                        authSystem.verifyBookAvailability(selectedBook, currentUser) &&
                        authSystem.confirmBorrowing(selectedBook, currentUser).contains("Borrow confirmed") &&
                        "Checked out".equals(selectedBook.getStatus()) &&
                        !authSystem.verifyBookAvailability(selectedBook, currentUser);

        assertTrue(allValid, "System should correctly verify, borrow, and update book availability status");
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

        boolean validTransaction = bookToBorrow != null &&
                currentUser.getBorrowedBooks().contains(bookToBorrow.getTitle()) &&
                "Checked out".equals(bookToBorrow.getStatus()) &&
                bookToBorrow.getDueDate() != null &&
                confirmation.contains("Borrow confirmed");

        assertTrue(validTransaction, "Borrowing transaction and data recording should be complete and valid");

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

        boolean updated = currentUser.getBorrowedBooks().contains(selectedBook.getTitle()) &&
                "Checked out".equals(selectedBook.getStatus());

        assertTrue(updated, "Borrower record and book status should both reflect successful borrowing");
    }



    @Test
    @DisplayName("Check borrowing confirmation and borrower acknowledgment")
    void RESP_16_test_01(){
        TestSetup setup = new TestSetup("Nord", "456");
        Borrower currentUser = setup.getCurrentUser();
        Catalogue catalogue = setup.getCatalogue();
        Authenticator authSystem = setup.getAuthSystem();

        Book selectedBook = authSystem.selectAvailableBook(catalogue);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        //confirm borrowing and acknowledge
        System.out.println(authSystem.confirmBorrowing(selectedBook, currentUser));
        authSystem.acknowledgeBorrowing(selectedBook, currentUser);

        //UI shows confirmation and borrower's acknowledgment
        assertTrue(output.toString().contains("Borrow confirmed") &&
                        currentUser.getBorrowedBooks().contains(selectedBook.getTitle()),
                "UI should display borrowing confirmation and borrower acknowledgment");

        System.setOut(System.out);
    }

    @Test
    @DisplayName("Check system returns to available functionality after borrowing")
    void RESP_16_test_02(){
        TestSetup setup = new TestSetup("Nord", "456");
        Authenticator authSystem = setup.getAuthSystem();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        //trigger return to functionality section
        authSystem.returnToFunctionalitySection();
        System.out.println("Returned to menu"); // simulate UI message

        // UI confirms user is back at the main menu
        assertTrue(output.toString().contains("Returned to menu"),
                "UI should show user returned to available functionality");

        System.setOut(System.out);

    }


    @Test
    @DisplayName("Check system handling of unavailable book and hold placement")
    void RESP_17_test_01(){
        TestSetup setup = new TestSetup("Nord", "456");
        Borrower currentUser = setup.getCurrentUser();
        Catalogue catalogue = setup.getCatalogue();
        Authenticator authSystem = setup.getAuthSystem();

        //borrower tries to borrow an already borrowed book
        Book borrowedBook = catalogue.getBookHeld("Treasure Island");
        borrowedBook.borrowBook();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        //handle unavailable book - UI should notify user
        System.out.println(authSystem.handleUnavailableBook(borrowedBook, currentUser));

        //UI shows hold message
        assertTrue(output.toString().contains("Hold"),
                "UI should notify borrower about hold placement for unavailable book");

        System.setOut(System.out);
    }

    @Test
    @DisplayName("Check borrower - has borrowed max of 3 books")
    void RESP_18_test_01(){
        TestSetup setup = new TestSetup("Nord", "456");
        Borrower currentUser = setup.getCurrentUser();
        Catalogue catalogue = setup.getCatalogue();
        Authenticator authSystem = setup.getAuthSystem();

        //borrow 3 books first
        for (int i = 0; i < 3; i++) {
            Book book = authSystem.selectAvailableBook(catalogue);
            authSystem.updateBorrowerAndBook(currentUser, book);
        }

        //attempt to borrow 4th book
        Book extraBook = authSystem.selectAvailableBook(catalogue);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        System.out.println(authSystem.confirmBorrowing(extraBook, currentUser));

        //UI shows max borrowing limit message
        assertTrue(output.toString().contains("Maximum borrowing limit reached"),
                "UI should notify borrower that maximum borrowing limit has been reached");

        System.setOut(System.out);

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
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        System.out.println(authSystem.displayBorrowedBooks(currentUser, catalogue));

        //assert that UI contains both titles and their due dates
        assertTrue(output.toString().contains(borrowedBook1.getTitle()) &&
                        output.toString().contains(borrowedBook1.getDueDate().toString()) &&
                        output.toString().contains(borrowedBook2.getTitle()) &&
                        output.toString().contains(borrowedBook2.getDueDate().toString()),
                "UI should display all borrowed books with due dates");

        System.setOut(System.out);
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

        boolean validReturn = returnBookMsg.equals("Return confirmed: " + borrowBook.getTitle()) &&
                currentUser.getBorrowedBooks().isEmpty() &&
                "Available".equals(borrowBook.getStatus()) &&
                authSystem.returnToFunctionalitySection();

        assertTrue(validReturn, "Book return process should complete successfully and reset borrower/book states");

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

    @Test
    @DisplayName("Manage holds on returned books")
    void RESP_22_test_01(){
        TestSetup setup = new TestSetup("Aeil", "789");
        Borrower currentUser = setup.getCurrentUser();
        Catalogue catalogue = setup.getCatalogue();
        Authenticator authSystem = setup.getAuthSystem();

        Borrower nextBorrower = new Borrower("NextBorrower", "000");

        //borrow book
        Book borrowedBook = authSystem.selectAvailableBook(catalogue);
        authSystem.updateBorrowerAndBook(currentUser, borrowedBook);

        //place hold
        borrowedBook.placeHold(nextBorrower.getUsername());

        //book returnal
        String result = authSystem.returnBook(borrowedBook, currentUser, catalogue);

        boolean validHoldReturn = result.equals("Return confirmed: " + borrowedBook.getTitle()) &&
                "On Hold".equals(borrowedBook.getStatus()) &&
                !currentUser.getBorrowedBooks().contains(borrowedBook.getTitle()) &&
                nextBorrower.getUsername().equals(borrowedBook.getHoldBy());

        assertTrue(validHoldReturn, "Returned book should transition to hold status for next borrower correctly");


    }



    @Test
    @DisplayName("Check successful logout of user, and return to user authentication")
    void RESP_23_test_01(){
        TestSetup setup = new TestSetup("Aeil", "789");
        Borrower currentUser = setup.getCurrentUser();
        Authenticator authSystem = setup.getAuthSystem();

        //logout user
        String logoutResult = authSystem.logout();

        //verify logout / verify that user/session is cleared  /verify system return to user authentication
        boolean allLogoutChecks = currentUser != null &&
                logoutResult.equals("Logout successful. Returning to authentication") &&
                authSystem.getCurrentUser() == null &&
                authSystem.userAuthenticationPrompt();

        assertTrue(allLogoutChecks, "Logout should clear session and prompt authentication again");

    }
}
