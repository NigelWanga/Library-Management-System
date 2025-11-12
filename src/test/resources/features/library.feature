Feature: Library Management System
  As a user
  I want to borrow and return books
  So that I can manage my reading access in the library database


  Scenario: Multi-user borrow and return with availability validated
    Given a library with the book "The Iliad" by "Homer"
    And a registered user "alice" with password "pass123"
    And a registered user "bob" with password "pass456"

    When "alice" logs in with password "pass123"
    And "alice" borrows "The Iliad"
    And "alice" logs out

    When "bob" logs in with password "pass456"
    Then "The Iliad" should be unavailable to borrow
    And "bob" logs out

    When "alice" logs in with password "pass123"
    And "alice" returns "The Iliad"
    And "alice" logs out

    When "bob" logs in with password "pass456"
    And "bob" borrows "The Iliad"
    Then "The Iliad" should be marked borrowed by "bob"


  Scenario: Multiple users placing holds and queue processing
    Given a library with the book "War and Peace" by "Leo Tolstoy"
    And a registered user "alice" with password "pass123"
    And a registered user "bob" with password "pass456"
    And a registered user "charlie" with password "pass789"

    #alice borrows book
    When "alice" logs in with password "pass123"
    And "alice" borrows "War and Peace"
    And "alice" logs out

    #bob tries to borrow same book but unavailable, places hold
    When "bob" logs in with password "pass456"
    And "bob" places a hold on "War and Peace"
    And "bob" logs out

    #charlie tries to borrow same book but unavailable, places hold
    When "charlie" logs in with password "pass789"
    And "charlie" places a hold on "War and Peace"
    And "charlie" logs out

    #alice returns book
    When "alice" logs in with password "pass123"
    And "alice" returns "War and Peace"

    #bob notified book is available
    Then "bob" should be notified that "War and Peace" is now available
    And "alice" logs out

    #bob can borrow book now
    When "bob" logs in with password "pass456"
    And "bob" borrows "War and Peace"
    And "bob" logs out

    Then "charlie" should now be first in the hold queue

  Scenario: Interaction between borrowing limits and holds
    Given a library with the book "Ulysses" by "James Joyce"
    And a library with the book "The Odyssey" by "Homer"
    And a library with the book "Jane Eyre" by "Charlotte Brontë"
    And a library with the book "Pride and Prejudice" by "Jane Austen"

    And a registered user "alice" with password "pass123"
    And a registered user "bob" with password "pass456"

    #alice borrows 3 books, reaches borrowing limit
    When "alice" logs in with password "pass123"
    And "alice" borrows "Ulysses"
    And "alice" borrows "The Odyssey"
    And "alice" borrows "Jane Eyre"

    Then "alice" should have 3 borrowed books
    And "alice" cannot borrow "Pride and Prejudice"

    #alice can place a hold even at limit
    And "alice" places a hold on "Pride and Prejudice"
    And "alice" logs out

    #bob borrows Pride and Prejudice
    When "bob" logs in with password "pass456"
    And "bob" borrows "Pride and Prejudice"
    And "bob" logs out

    #alice returns one of her books → drops below limit
    When "alice" logs in with password "pass123"
    And "alice" returns "Ulysses"
    And "alice" logs out

    #bob returns Pride and Prejudice
    When "bob" logs in with password "pass456"
    And "bob" returns "Pride and Prejudice"

    #alice should now be notified that Pride and Prejudice is available
    Then "alice" should be notified that "Pride and Prejudice" is now available


  Scenario: No books borrowed and the system displays an informative message
    Given a library with the book "Animal Farm" by "George Orwell"
    And a library with the book "Wuthering Heights" by "Emily Brontë"
    And a registered user "alice" with password "pass123"
    And a registered user "bob" with password "pass456"


    When "alice" logs in with password "pass123"
    Then "alice" should see no borrowed books
    And all books should be available
    And "alice" logs out

    When "bob" logs in with password "pass456"
    Then "bob" should see no borrowed books
    And all books should be available
