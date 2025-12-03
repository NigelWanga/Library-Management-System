describe('Library Book Management', () => {

  beforeEach(() => {
    // Reset the library to initial state before each test
    // This is the ONLY allowed use of cy.request() as per rubric
    cy.request('POST', 'http://localhost:3000/api/reset');
    cy.visit('http://localhost:3000');
  });

  // ============================================
  // SCENARIO 1: Basic Borrow-Return Cycle (10%)
  // ============================================

  it('should demonstrate basic borrow-return cycle where borrowed book becomes unavailable to other users', () => {

    // Alice logs in
    cy.get('[data-testid="username-input"]').type('alice');
    cy.get('[data-testid="password-input"]').type('pass123');
    cy.get('[data-testid="login-btn"]').click();

    // Verify Alice is logged in
    // This verifies that the authentication system works correctly
    cy.get('[data-testid="current-user"]')
      .should('contain', 'alice');

    // Alice borrows "1984"
    cy.get('[data-testid="borrow-btn-1984"]').click();

    // Verify success message appears
    // This confirms the UI provides feedback for the borrow action
    cy.get('[data-testid="message-success"]')
      .should('be.visible')
      .and('contain', 'borrowed successfully');

    // Wait for UI to update after borrow
    cy.wait(1000);

    // Verify book status changed to "Checked Out"
    // This ensures the book availability is correctly reflected in the UI
    cy.get('[data-testid="book-card-1984"]')
      .find('[data-testid="book-status"]')
      .should('contain', 'Checked Out');

    // Verify borrow button is disabled for this book
    // This demonstrates that a borrowed book cannot be borrowed again
    cy.get('[data-testid="borrow-btn-1984"]')
      .should('be.disabled');

    // Verify return button is now enabled
    // This confirms the user can return the book they just borrowed
    cy.get('[data-testid="return-btn-1984"]')
      .should('not.be.disabled');

    // Verify borrowed count increased to 1/3
    // This confirms the user's borrowed book count is tracked correctly
    cy.get('[data-testid="borrowed-count"]')
      .should('contain', '1/3');

    // Alice logs out
    cy.get('[data-testid="logout-btn"]').click();

    // Bob logs in
    cy.get('[data-testid="username-input"]').type('bob');
    cy.get('[data-testid="password-input"]').type('pass456');
    cy.get('[data-testid="login-btn"]').click();

    // Wait for Bob's page to load
    cy.wait(500);

    // Verify Bob cannot borrow the same book (button disabled)
    // This verifies that only one user can have a book at a time
    cy.get('[data-testid="borrow-btn-1984"]')
      .should('be.disabled');

    // Verify book shows as "Checked Out" for Bob
    // This confirms book availability is consistent across different user sessions
    cy.get('[data-testid="book-card-1984"]')
      .find('[data-testid="book-status"]')
      .should('contain', 'Checked Out');

    // Bob logs out
    cy.get('[data-testid="logout-btn"]').click();

    // Alice logs back in
    cy.get('[data-testid="username-input"]').type('alice');
    cy.get('[data-testid="password-input"]').type('pass123');
    cy.get('[data-testid="login-btn"]').click();

    // Wait for Alice's page to load
    cy.wait(500);

    // Alice returns "1984"
    cy.get('[data-testid="return-btn-1984"]').click();

    // Verify return success message
    // This confirms the UI acknowledges the return action
    cy.get('[data-testid="message-success"]')
      .should('be.visible')
      .and('contain', 'returned successfully');

    // Wait for UI to update after return
    cy.wait(1000);

    // Verify book status changed back to "Available"
    // This ensures the returned book becomes available again
    cy.get('[data-testid="book-card-1984"]')
      .find('[data-testid="book-status"]')
      .should('contain', 'Available');

    // Verify borrowed count decreased to 0/3
    // This confirms the borrowed count is updated after return
    cy.get('[data-testid="borrowed-count"]')
      .should('contain', '0/3');

    // Alice logs out
    cy.get('[data-testid="logout-btn"]').click();

    // Bob logs in again
    cy.get('[data-testid="username-input"]').type('bob');
    cy.get('[data-testid="password-input"]').type('pass456');
    cy.get('[data-testid="login-btn"]').click();

    // Wait for Bob's page to load
    cy.wait(500);

    // Verify Bob can now borrow the book (button enabled)
    // This demonstrates that a returned book becomes available to other users
    cy.get('[data-testid="borrow-btn-1984"]')
      .should('not.be.disabled');

    // Verify book status is "Available"
    // This confirms the book is truly available in the system
    cy.get('[data-testid="book-card-1984"]')
      .find('[data-testid="book-status"]')
      .should('contain', 'Available');
  });

  // ============================================
  // SCENARIO 2: Multiple Holds Queue Processing (45%)
  // ============================================

  it('should process hold queue in FIFO order and notify correct users', () => {

    // Alice logs in and borrows "The Hobbit"
    cy.get('[data-testid="username-input"]').type('alice');
    cy.get('[data-testid="password-input"]').type('pass123');
    cy.get('[data-testid="login-btn"]').click();
    cy.get('[data-testid="borrow-btn-the-hobbit"]').click();
    cy.wait(1000);

    // Verify Alice borrowed the book successfully
    // This confirms the initial borrow action worked
    cy.get('[data-testid="borrowed-count"]').should('contain', '1/3');
    cy.get('[data-testid="logout-btn"]').click();

    // Bob logs in and places hold on "The Hobbit" (first in queue)
    cy.get('[data-testid="username-input"]').type('bob');
    cy.get('[data-testid="password-input"]').type('pass456');
    cy.get('[data-testid="login-btn"]').click();
    cy.wait(500);
    cy.get('[data-testid="hold-btn-the-hobbit"]').click();

    // Verify hold was placed successfully
    // This confirms users can place holds on unavailable books
    cy.get('[data-testid="message-success"]')
      .should('contain', 'Hold placed successfully');

    cy.wait(1000);
    cy.get('[data-testid="logout-btn"]').click();

    // Charlie logs in and places hold on "The Hobbit" (second in queue)
    cy.get('[data-testid="username-input"]').type('charlie');
    cy.get('[data-testid="password-input"]').type('pass789');
    cy.get('[data-testid="login-btn"]').click();
    cy.wait(500);
    cy.get('[data-testid="hold-btn-the-hobbit"]').click();

    // Verify Charlie's hold was placed (position 2 in queue)
    // This demonstrates the FIFO queue is accepting multiple holds
    cy.get('[data-testid="message-success"]')
      .should('contain', 'Hold placed successfully');

    cy.wait(1000);
    cy.get('[data-testid="logout-btn"]').click();

    // Alice returns "The Hobbit"
    cy.get('[data-testid="username-input"]').type('alice');
    cy.get('[data-testid="password-input"]').type('pass123');
    cy.get('[data-testid="login-btn"]').click();
    cy.wait(500);
    cy.get('[data-testid="return-btn-the-hobbit"]').click();

    // Verify return message mentions Bob was notified (first in queue)
    // This confirms the hold queue follows FIFO ordering
    cy.get('[data-testid="message-success"]')
      .should('contain', 'bob')
      .and('contain', 'notified');

    cy.wait(1000);

    // Verify book status is now "On Hold"
    // This confirms the book is reserved for the next user in queue
    cy.get('[data-testid="book-card-the-hobbit"]')
      .find('[data-testid="book-status"]')
      .should('contain', 'On Hold');

    cy.get('[data-testid="logout-btn"]').click();

    // Bob logs in and checks notifications
    cy.get('[data-testid="username-input"]').type('bob');
    cy.get('[data-testid="password-input"]').type('pass456');
    cy.get('[data-testid="login-btn"]').click();
    cy.wait(500);

    // Verify Bob has a notification
    // This confirms notifications are sent to the correct user
    cy.get('[data-testid="view-notifications"]').click();
    cy.wait(500);
    cy.get('[data-testid="notification-0"]')
      .should('be.visible')
      .find('[data-testid="notification-message"]')
      .should('contain', 'The Hobbit')
      .and('contain', 'available');

    // Switch back to all books view
    cy.get('[data-testid="view-all-books"]').click();
    cy.wait(1000);

    // Verify book shows "Reserved for you" for Bob
    // This confirms the UI indicates the book is reserved for the notified user
    cy.get('[data-testid="book-card-the-hobbit"]')
      .should('contain', 'Reserved for you');

    // Verify borrow button is enabled for Bob (since book is reserved for him)
    // This confirms only the notified user can borrow the reserved book
    cy.get('[data-testid="borrow-btn-the-hobbit"]')
      .should('not.be.disabled');

    // Bob borrows the book
    cy.get('[data-testid="borrow-btn-the-hobbit"]').click();

    // Verify Bob successfully borrowed the reserved book
    // This demonstrates only the notified user can borrow the reserved book
    cy.get('[data-testid="message-success"]')
      .should('contain', 'borrowed successfully');

    cy.wait(1000);
    cy.get('[data-testid="logout-btn"]').click();

    // Charlie tries to borrow but cannot (button disabled)
    cy.get('[data-testid="username-input"]').type('charlie');
    cy.get('[data-testid="password-input"]').type('pass789');
    cy.get('[data-testid="login-btn"]').click();
    cy.wait(500);

    // Verify borrow button is disabled for Charlie
    // This confirms only the notified user (Bob) could borrow the book
    cy.get('[data-testid="borrow-btn-the-hobbit"]')
      .should('be.disabled');

    // Verify book shows as "Checked Out"
    // This confirms the book state after Bob borrowed it
    cy.get('[data-testid="book-card-the-hobbit"]')
      .find('[data-testid="book-status"]')
      .should('contain', 'Checked Out');

    cy.get('[data-testid="logout-btn"]').click();

    // Bob returns the book
    cy.get('[data-testid="username-input"]').type('bob');
    cy.get('[data-testid="password-input"]').type('pass456');
    cy.get('[data-testid="login-btn"]').click();
    cy.wait(500);
    cy.get('[data-testid="return-btn-the-hobbit"]').click();

    // Verify Charlie is notified (next in queue)
    // This demonstrates the queue advances properly when books are returned
    cy.get('[data-testid="message-success"]')
      .should('contain', 'charlie')
      .and('contain', 'notified');

    cy.wait(1000);
    cy.get('[data-testid="logout-btn"]').click();

    // Charlie logs in and checks notifications
    cy.get('[data-testid="username-input"]').type('charlie');
    cy.get('[data-testid="password-input"]').type('pass789');
    cy.get('[data-testid="login-btn"]').click();
    cy.wait(500);

    cy.get('[data-testid="view-notifications"]').click();
    cy.wait(500);

    // Verify Charlie received notification
    // This confirms the queue processed correctly and notified the second user
    cy.get('[data-testid="notification-0"]')
      .should('be.visible')
      .find('[data-testid="notification-message"]')
      .should('contain', 'The Hobbit');

    // Charlie can now borrow the book
    cy.get('[data-testid="view-all-books"]').click();
    cy.wait(1000);

    // Verify book is reserved for Charlie
    // This confirms the FIFO queue advanced to the next user correctly
    cy.get('[data-testid="book-card-the-hobbit"]')
      .should('contain', 'Reserved for you');

    cy.get('[data-testid="borrow-btn-the-hobbit"]').click();

    // Verify Charlie successfully borrowed the book
    // This confirms the complete hold queue workflow processed correctly
    cy.get('[data-testid="message-success"]')
      .should('contain', 'borrowed successfully');
  });

  // ============================================
  // SCENARIO 3: Borrowing Limit and Hold Interactions (45%)
  // ============================================

  it('should enforce 3-book limit while allowing holds and processing them when capacity opens', () => {

    // Alice logs in
    cy.get('[data-testid="username-input"]').type('alice');
    cy.get('[data-testid="password-input"]').type('pass123');
    cy.get('[data-testid="login-btn"]').click();
    cy.wait(500);

    // Alice borrows 3 books to reach the limit
    cy.get('[data-testid="borrow-btn-1984"]').click();
    cy.wait(1000);
    cy.get('[data-testid="borrow-btn-the-great-gatsby"]').click();
    cy.wait(1000);
    cy.get('[data-testid="borrow-btn-the-hobbit"]').click();
    cy.wait(1000);

    // Verify borrowed count is 3/3
    // This confirms the UI tracks the borrowing count correctly
    cy.get('[data-testid="borrowed-count"]')
      .should('contain', '3/3');

    // Try to borrow a 4th book ("Pride and Prejudice")
    cy.get('[data-testid="borrow-btn-pride-and-prejudice"]').click();

    // Verify error message about borrowing limit
    // This demonstrates the 3-book borrowing limit is enforced
    cy.get('[data-testid="message-error"]')
      .should('be.visible')
      .and('contain', 'limit');

    cy.wait(1000);

    // Verify borrowed count still shows 3/3
    // This confirms the limit was enforced and no 4th book was borrowed
    cy.get('[data-testid="borrowed-count"]')
      .should('contain', '3/3');

    cy.get('[data-testid="logout-btn"]').click();

    // Bob logs in and borrows "Harry Potter"
    cy.get('[data-testid="username-input"]').type('bob');
    cy.get('[data-testid="password-input"]').type('pass456');
    cy.get('[data-testid="login-btn"]').click();
    cy.wait(500);
    cy.get('[data-testid="borrow-btn-harry-potter"]').click();
    cy.wait(1000);

    // Verify Bob borrowed the book
    // This sets up the scenario where Alice will place a hold
    cy.get('[data-testid="borrowed-count"]').should('contain', '1/3');
    cy.get('[data-testid="logout-btn"]').click();

    // Alice (at limit) places hold on "Harry Potter"
    cy.get('[data-testid="username-input"]').type('alice');
    cy.get('[data-testid="password-input"]').type('pass123');
    cy.get('[data-testid="login-btn"]').click();
    cy.wait(500);

    // Verify Alice still has 3/3 books
    // This confirms Alice is at the borrowing limit
    cy.get('[data-testid="borrowed-count"]')
      .should('contain', '3/3');

    cy.get('[data-testid="hold-btn-harry-potter"]').click();

    // Verify hold was placed successfully even at limit
    // This demonstrates users can place holds even when at the borrowing limit
    cy.get('[data-testid="message-success"]')
      .should('be.visible')
      .and('contain', 'Hold placed successfully');

    cy.wait(1000);

    // Alice returns one book ("1984") to drop below limit
    cy.get('[data-testid="return-btn-1984"]').click();
    cy.wait(1000);

    // Verify borrowed count is now 2/3
    // This confirms borrowing capacity increases after return
    cy.get('[data-testid="borrowed-count"]')
      .should('contain', '2/3');

    cy.get('[data-testid="logout-btn"]').click();

    // Bob returns "Harry Potter"
    cy.get('[data-testid="username-input"]').type('bob');
    cy.get('[data-testid="password-input"]').type('pass456');
    cy.get('[data-testid="login-btn"]').click();
    cy.wait(500);
    cy.get('[data-testid="return-btn-harry-potter"]').click();

    // Verify Alice is notified about "Harry Potter"
    // This confirms when a user at limit returns a book and is next in queue, they get notified
    cy.get('[data-testid="message-success"]')
      .should('contain', 'alice')
      .and('contain', 'notified');

    cy.wait(1000);
    cy.get('[data-testid="logout-btn"]').click();

    // Alice logs back in and checks notifications
    cy.get('[data-testid="username-input"]').type('alice');
    cy.get('[data-testid="password-input"]').type('pass123');
    cy.get('[data-testid="login-btn"]').click();
    cy.wait(500);

    cy.get('[data-testid="view-notifications"]').click();
    cy.wait(500);

    // Verify Alice received notification for "Harry Potter"
    // This confirms notification was sent when user returned book while at limit
    cy.get('[data-testid="notification-0"]')
      .should('be.visible')
      .find('[data-testid="notification-message"]')
      .should('contain', 'Harry Potter')
      .and('contain', 'available');

    cy.get('[data-testid="view-all-books"]').click();
    cy.wait(1000);

    // Verify borrowed count is 2/3 (has capacity)
    // This confirms Alice now has capacity to borrow the reserved book
    cy.get('[data-testid="borrowed-count"]')
      .should('contain', '2/3');

    // Alice borrows "Harry Potter"
    cy.get('[data-testid="borrow-btn-harry-potter"]').click();

    // Verify successful borrow
    // This demonstrates the complete workflow: user at limit places hold, returns book, gets notified, and can borrow
    cy.get('[data-testid="message-success"]')
      .should('contain', 'borrowed successfully');

    cy.wait(1000);

    // Verify borrowed count is now 3/3 again
    // This confirms the final state after borrowing the held book
    cy.get('[data-testid="borrowed-count"]')
      .should('contain', '3/3');
  });

});