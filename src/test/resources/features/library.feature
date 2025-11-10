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