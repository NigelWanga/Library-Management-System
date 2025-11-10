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
        boolean success = authenticator.login(username, password);
        assertTrue(success);
    }

    @When("{string} borrows {string}")
    public void borrows(String username, String title) {
        Book selected = catalogue.findBookByTitle(title);
        Borrower borrower = authenticator.getCurrentUser();

        String result = authenticator.confirmBorrowing(selected, borrower);
        assertTrue(result.startsWith("Borrow confirmed"));
    }

    @When("{string} logs out")
    public void logs_out(String username) {
        authenticator.logout();
        assertNull(authenticator.getCurrentUser());
    }

    @Then("{string} should be unavailable to borrow")
    public void should_be_unavailable_to_borrow(String title) {
        Book selected = catalogue.findBookByTitle(title);
        assertFalse(selected.isAvailable());
    }

    @When("{string} returns {string}")
    public void returns(String username, String title) {
        Borrower borrower = authenticator.getCurrentUser();
        Book selected = catalogue.findBookByTitle(title);

        String message = authenticator.returnBook(selected, borrower, catalogue);
        assertTrue(message.startsWith("Return confirmed"));
    }

    @Then("{string} should be marked borrowed by {string}")
    public void should_be_marked_borrowed_by(String title, String username) {
        Book selected = catalogue.findBookByTitle(title);
        assertTrue(selected.isBorrowed());
    }

}