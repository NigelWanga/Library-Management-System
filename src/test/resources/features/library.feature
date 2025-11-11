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