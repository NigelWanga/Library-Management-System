// Library Management System - Business Logic (Converted from Java)

class Book {
  constructor(title, author) {
    this.title = title;
    this.author = author;
    this.isBorrowed = false;
    this.isOnHold = false;
    this.dueDate = null;
    this.onHoldBy = null;
    this.holdQueue = []; // FIFO queue for multiple holds
  }

  isAvailable() {
    return !this.isBorrowed;
  }

  borrowBook() {
    this.isBorrowed = true;
    const now = new Date();
    this.dueDate = new Date(now.getTime() + 14 * 24 * 60 * 60 * 1000); // 14 days from now
  }

  returnBook() {
    this.isBorrowed = false;
    this.dueDate = null;
    this.isOnHold = false;
  }

  placeHold(username) {
    this.onHoldBy = username;
  }

  addHoldQueue(username) {
    this.holdQueue.push(username);
  }

  hasQueuedHolds() {
    return this.holdQueue.length > 0;
  }

  peekHold() {
    return this.holdQueue[0] || null;
  }

  popHold() {
    return this.holdQueue.shift() || null;
  }

  getStatus() {
    if (this.isBorrowed) return 'Checked Out';
    if (this.isOnHold) return 'On Hold';
    return 'Available';
  }

  getDueDateString() {
    if (!this.dueDate) return null;
    return this.dueDate.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }
}

class Borrower {
  constructor(username, password) {
    this.username = username;
    this.password = password;
    this.borrowedBooks = [];
    this.heldBooks = [];
  }

  getBorrowedBooksCount() {
    return this.borrowedBooks.length;
  }

  addBorrowedBook(title) {
    this.borrowedBooks.push(title);
  }

  removeBorrowedBook(title) {
    const index = this.borrowedBooks.indexOf(title);
    if (index > -1) {
      this.borrowedBooks.splice(index, 1);
    }
  }

  hasHeldBook(title) {
    return this.heldBooks.includes(title);
  }

  addHeldBook(title) {
    this.heldBooks.push(title);
  }

  removeHeldBook(title) {
    const index = this.heldBooks.indexOf(title);
    if (index > -1) {
      this.heldBooks.splice(index, 1);
    }
  }
}

class Library {
  constructor() {
    this.books = [];
    this.borrowers = [];
    this.notifications = {}; // username -> [notifications]
    this.initialize();
  }

  initialize() {
    // Initialize borrowers (baseline data)
    this.borrowers = [
      new Borrower('alice', 'pass123'),
      new Borrower('bob', 'pass456'),
      new Borrower('charlie', 'pass789')
    ];

    // Initialize notifications
    this.borrowers.forEach(b => {
      this.notifications[b.username] = [];
    });

    // Initialize books (20 books as per rubric)
    const bookList = [
      ['The Great Gatsby', 'F. Scott Fitzgerald'],
      ['To Kill a Mockingbird', 'Harper Lee'],
      ['1984', 'George Orwell'],
      ['Pride and Prejudice', 'Jane Austen'],
      ['The Hobbit', 'J.R.R. Tolkien'],
      ['Harry Potter', 'J.K. Rowling'],
      ['The Catcher in the Rye', 'J.D. Salinger'],
      ['Animal Farm', 'George Orwell'],
      ['Lord of the Flies', 'William Golding'],
      ['Jane Eyre', 'Charlotte Brontë'],
      ['Wuthering Heights', 'Emily Brontë'],
      ['Moby Dick', 'Herman Melville'],
      ['The Odyssey', 'Homer'],
      ['Hamlet', 'William Shakespeare'],
      ['War and Peace', 'Leo Tolstoy'],
      ['The Divine Comedy', 'Dante Alighieri'],
      ['Crime and Punishment', 'Fyodor Dostoevsky'],
      ['Don Quixote', 'Miguel de Cervantes'],
      ['The Iliad', 'Homer'],
      ['Ulysses', 'James Joyce']
    ];

    this.books = bookList.map(([title, author]) => new Book(title, author));
  }

  reset() {
    this.books = [];
    this.borrowers = [];
    this.notifications = {};
    this.initialize();
  }

  // Authentication
  validateCredentials(username, password) {
    const borrower = this.findBorrower(username);
    if (!borrower) return false;
    return borrower.password === password;
  }

  findBorrower(username) {
    return this.borrowers.find(b => b.username === username);
  }

  findBook(title) {
    return this.books.find(b => b.title === title);
  }

  // Get all books with their status
  getAllBooks() {
    return this.books.map(book => ({
      title: book.title,
      author: book.author,
      status: book.getStatus(),
      dueDate: book.getDueDateString(),
      onHoldBy: book.onHoldBy,
      queueLength: book.holdQueue.length
    }));
  }

  // Borrow a book
  borrowBook(username, bookTitle) {
    const borrower = this.findBorrower(username);
    const book = this.findBook(bookTitle);

    if (!borrower) return { success: false, message: 'User not found' };
    if (!book) return { success: false, message: 'Book not found' };

    // Check if already borrowed by this user
    if (borrower.borrowedBooks.includes(bookTitle)) {
      return { success: false, message: 'You have already borrowed this book' };
    }

    // Check borrowing limit
    if (borrower.getBorrowedBooksCount() >= 3) {
      return { success: false, message: 'Borrowing limit reached (maximum 3 books)' };
    }

    // Check if book is available
    if (!book.isAvailable()) {
      return { success: false, message: 'Book is not available' };
    }

    // Check if book is on hold for someone else
    if (book.isOnHold && book.onHoldBy !== username) {
      return { success: false, message: 'Book is on hold for another user' };
    }

    // Borrow the book
    book.borrowBook();
    borrower.addBorrowedBook(bookTitle);

    // Clear hold if it was for this user
    if (book.onHoldBy === username) {
      book.isOnHold = false;
      book.onHoldBy = null;
      borrower.removeHeldBook(bookTitle);
      this.clearNotification(username, bookTitle);
    }

    return {
      success: true,
      message: `Book borrowed successfully. Due date: ${book.getDueDateString()}`,
      dueDate: book.getDueDateString()
    };
  }

  // Return a book
  returnBook(username, bookTitle) {
    const borrower = this.findBorrower(username);
    const book = this.findBook(bookTitle);

    if (!borrower) return { success: false, message: 'User not found' };
    if (!book) return { success: false, message: 'Book not found' };

    // Check if borrower actually borrowed this book
    if (!borrower.borrowedBooks.includes(bookTitle)) {
      return { success: false, message: 'You have not borrowed this book' };
    }

    // Remove from borrower's list
    borrower.removeBorrowedBook(bookTitle);

    // Handle hold queue
    if (book.hasQueuedHolds()) {
      const nextUser = book.popHold();
      book.isBorrowed = false;
      book.isOnHold = true;
      book.onHoldBy = nextUser;
      this.addNotification(nextUser, bookTitle);
      return {
        success: true,
        message: `Book returned. The user who placed a hold has been notified.`
      };
    }

    // No holds - just return the book
    book.returnBook();
    return { success: true, message: 'Book returned successfully' };
  }

  // Place a hold on a book
  placeHold(username, bookTitle) {
    const borrower = this.findBorrower(username);
    const book = this.findBook(bookTitle);

    if (!borrower) return { success: false, message: 'User not found' };
    if (!book) return { success: false, message: 'Book not found' };

    // Check if already borrowed by this user
    if (borrower.borrowedBooks.includes(bookTitle)) {
      return { success: false, message: 'You already have this book checked out' };
    }

    // Check if already has hold
    if (borrower.hasHeldBook(bookTitle)) {
      return { success: false, message: 'You already have a hold on this book' };
    }

    // If book is available, should borrow instead
    if (book.isAvailable() && !book.isOnHold) {
      return { success: false, message: 'Book is available - please borrow it instead' };
    }

    // Place hold
    book.addHoldQueue(username);
    borrower.addHeldBook(bookTitle);

    return {
      success: true,
      message: `Hold placed successfully. Position in queue: ${book.holdQueue.length}`,
      queuePosition: book.holdQueue.length
    };
  }

  // Get borrowed books for a user
  getBorrowedBooks(username) {
    const borrower = this.findBorrower(username);
    if (!borrower) return [];

    return borrower.borrowedBooks.map(title => {
      const book = this.findBook(title);
      return {
        title: book.title,
        author: book.author,
        dueDate: book.getDueDateString()
      };
    });
  }

  // Notification system
  addNotification(username, bookTitle) {
    if (!this.notifications[username]) {
      this.notifications[username] = [];
    }
    this.notifications[username].push({
      message: `"${bookTitle}" is now available for you to borrow`,
      bookTitle: bookTitle,
      timestamp: new Date().toISOString()
    });
  }

  clearNotification(username, bookTitle) {
    if (this.notifications[username]) {
      this.notifications[username] = this.notifications[username].filter(
        n => n.bookTitle !== bookTitle
      );
    }
  }

  getNotifications(username) {
    return this.notifications[username] || [];
  }

  clearAllNotifications(username) {
    this.notifications[username] = [];
  }
}

module.exports = Library;