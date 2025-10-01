package org.example;

public class InitializeLibrary {

    Catalogue catalogue = new Catalogue();

    public Catalogue initializeLibrary(){
        catalogue.addBook(new Book("Great Gatsby", "F. Scott FitzGerald"));
        catalogue.addBook(new Book("The Adventures of Huckleberry Finn", "Mark Twain"));
        catalogue.addBook(new Book("Treasure Island", "Robert Louis Stevenson"));
        catalogue.addBook(new Book("Pride and Prejudice", "Jane Austen"));
        catalogue.addBook(new Book("Wuthering Heights", "Emily Brontë"));
        catalogue.addBook(new Book("Jane Eyre", "Charlotte Brontë"));
        catalogue.addBook(new Book("Moby Dick", "Herman Melville"));
        catalogue.addBook(new Book("The Scarlet Letter", "Nathaniel Hawthorne"));
        catalogue.addBook(new Book("Gulliver's Travels", "Jonathan Swift"));
        catalogue.addBook(new Book("The Pilgrim's Progress", "John Bunyan"));
        catalogue.addBook(new Book("A Christmas Carol", "Charles Dickens"));
        catalogue.addBook(new Book("A Tale of Two Cities", "Charles Dickens"));
        catalogue.addBook(new Book("Little Women", "Louisa May Alcott"));
        catalogue.addBook(new Book("Great Expectations", "Charles Dickens"));
        catalogue.addBook(new Book("Oliver Twist", "Charles Dickens"));
        catalogue.addBook(new Book("Crime and Punishment", "Fyodor Dostoyevsky"));
        catalogue.addBook(new Book("The Return of the King", "J.R.R. Tolkien"));
        catalogue.addBook(new Book("Brave New World", "Aldous Huxley"));
        catalogue.addBook(new Book("War and Peace", "Leo Tolstoy"));
        catalogue.addBook(new Book("To Kill a Mockingbird", "Harper Lee"));
        return catalogue;
    }
}
