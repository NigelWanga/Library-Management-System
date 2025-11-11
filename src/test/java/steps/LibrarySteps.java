package steps;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.example.Catalogue;
import org.example.BorrowerRegistry;
import org.example.Borrower;
import org.example.Authenticator;
import org.example.Book;


import static org.junit.jupiter.api.Assertions.*;


public class LibrarySteps {

    Catalogue catalogue;
    BorrowerRegistry registry;
    Authenticator authenticator;
    Book book;

    //message results for our THEN assertions
    String finalMessage; //result from returnBook - Authenticator
    boolean lastLogin; //result of last login attempt
    String lastBorrow;  //string result from confirmBorrowing - Authenticator

    @Given("a library with the book {string} by {string}")
    public void a_library_with_the_book_by(String title, String author) {
        catalogue = new Catalogue();
        registry = new BorrowerRegistry();
        authenticator = new Authenticator(registry);

        book = new Book(title, author);
        catalogue.addBook(book);
    }

    @Given("a registered user {string} with password {string}")
    public void a_registered_user_with_password(String username, String password) {
        Borrower borrower = new Borrower(username, password);
        registry.addBorrower(borrower);
    }

    @When("{string} logs in with password {string}")
    public void logs_in_with_password(String username, String password) {
        lastLogin = authenticator.login(username, password);
    }

    @When("{string} borrows {string}")
    public void borrows(String username, String title) {
        Book selected = catalogue.findBookByTitle(title);
        Borrower borrower = authenticator.getCurrentUser();

        lastBorrow = authenticator.confirmBorrowing(selected, borrower);
    }

    @When("{string} places a hold on {string}")
    public void places_a_hold_on(String username, String title) {
        Book selected = catalogue.findBookByTitle(title);
        selected.addHoldQueue(username);
    }

    @When("{string} logs out")
    public void logs_out(String username) {
        finalMessage = authenticator.logout();
    }

    @When("{string} returns {string}")
    public void returns(String username, String title) {
        Borrower borrower = authenticator.getCurrentUser();
        Book selected = catalogue.findBookByTitle(title);

        finalMessage = authenticator.returnBook(selected, borrower, catalogue);
    }

    @Then("{string} should be unavailable to borrow")
    public void should_be_unavailable_to_borrow(String title) {
        Book selected = catalogue.findBookByTitle(title);
        assertNotNull(selected, "Book should exist");
        assertFalse(selected.isAvailable(), "Book should be unavailable");
    }

    @Then("{string} should be marked borrowed by {string}")
    public void should_be_marked_borrowed_by(String title, String username) {
        Book selected = catalogue.findBookByTitle(title);
        assertNotNull(selected, "Book should exist");
        assertTrue(selected.isBorrowed(), "Book should be marked borrowed");
    }

    @Then("{string} should be notified that {string} is now available")
    public void should_be_notified_that_is_now_available(String username, String title) {
        assertNotNull(finalMessage, "No message should be available");
        assertTrue(finalMessage.contains(username), "Message should contain username");
        assertTrue(finalMessage.contains(title), "Message should contain title");
    }

    @Then("{string} should now be first in the hold queue")
    public void should_now_be_first_in_the_hold_queue(String username) {
        Book selected = catalogue.findBookByTitle("War and Peace");
        assertNotNull(selected, "Book should exist");
        String check = selected.peekHold();
        assertEquals(username, check, "first at hold queue should be expected user");
    }

}