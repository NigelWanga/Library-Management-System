package org.example;

import java.util.ArrayList;
import java.util.Scanner;

public class Library {

    static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        // initialize systems only ONCE
        InitializeBorrowers initBorrowers = new InitializeBorrowers();
        BorrowerRegistry registry = initBorrowers.initializeBorrowers();
        InitializeLibrary initLibrary = new InitializeLibrary();
        Catalogue catalogue = initLibrary.initializeLibrary();

        Authenticator authSystem = new Authenticator(registry);

        System.out.println("=== Welcome to the Library System ===");

        boolean systemExit = false;

        //allows multiple logins before exiting system entirely
        while (!systemExit) {
            Borrower currentUser = authSystem.loginPrompt(input);
            if (currentUser == null) {
                System.out.println("Login failed. Returning to login screen...");
                continue;
            }

            //setting current user to session
            authSystem.setCurrentUser(currentUser);
            System.out.println("Hello, " + currentUser.getUsername() + "!");

            boolean sessionActive = true;

            //display operations to user
            while (sessionActive) {
                System.out.println("\n--- Menu ---");
                System.out.println("1. Display all books");
                System.out.println("2. Borrow a book");
                System.out.println("3. Return a book");
                System.out.println("4. View borrowed books");
                System.out.println("5. Logout");
                System.out.println("6. Exit System");
                System.out.print("Choose an option: ");
                String choice = input.nextLine();

                switch (choice) {
                    case "1":   //displays catalogue to user
                        catalogue.displayAllBooks();
                        break;

                    case "2": //enables user to enter title of book to borrow
                        System.out.print("Enter title of book to borrow: ");
                        String borrowTitle = input.nextLine();
                        Book bookToBorrow = catalogue.findBookByTitle(borrowTitle);

                        if (bookToBorrow == null) {
                            System.out.println("Book not found.");
                            break;
                        }

                        if (!bookToBorrow.isAvailable()) {
                            System.out.println("That book is currently Checked Out and not available.");
                            break;
                        }

                        if (currentUser.getBorrowedBooks().size() >= 3) {
                            System.out.println("Maximum borrowing limit reached.");
                            break;
                        }

                        String confirmMessage = authSystem.confirmBorrowing(bookToBorrow, currentUser);
                        System.out.println(confirmMessage);
                        break;

                    case "3":
                        System.out.print("Enter title of book to return: ");
                        String returnTitle = input.nextLine();
                        Book bookToReturn = catalogue.findBookByTitle(returnTitle);

                        if (bookToReturn != null) {
                            String returnMsg = authSystem.returnBook(bookToReturn, currentUser, catalogue);
                            System.out.println(returnMsg);
                        } else {
                            System.out.println("Book not found.");
                        }
                        break;

                    case "4": //display books borrowed
                        ArrayList<String> borrowed = currentUser.getBorrowedBooks();
                        if (borrowed.isEmpty()) {
                            System.out.println("You have not borrowed any books.");
                        } else {
                            System.out.println("Your borrowed books:");
                            for (String b : borrowed) System.out.println(" - " + b);
                        }
                        break;

                    case "5":
                        System.out.println(authSystem.logout());
                        sessionActive = false; //end current session, go back to login screen
                        break;

                    case "6":
                        System.out.println("System shutting down... Goodbye!");
                        systemExit = true;  //end outer loop entirely
                        sessionActive = false;
                        break;

                    default:
                        System.out.println("Invalid option. Try again.");
                }
            }
        }

        input.close();
    }
}
