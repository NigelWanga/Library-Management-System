const express = require('express');
const path = require('path');
const Library = require('./library');

const app = express();
const PORT = 3000;

// Middleware
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Serve static files EXCEPT index.html (we handle that route specifically)
app.use(express.static('public', { index: false }));

// Initialize library
const library = new Library();
let currentUser = null;

// ============================================
// API ROUTES
// ============================================

// Login
app.post('/api/login', (req, res) => {
  const { username, password } = req.body;

  if (library.validateCredentials(username, password)) {
    currentUser = username;
    res.json({ success: true, username: username });
  } else {
    res.json({ success: false, message: 'Invalid username or password' });
  }
});

// Logout
app.post('/api/logout', (req, res) => {
  currentUser = null;
  res.json({ success: true, message: 'Logged out successfully' });
});

// Get current user
app.get('/api/current-user', (req, res) => {
  if (!currentUser) {
    return res.json({ loggedIn: false });
  }
  res.json({ loggedIn: true, username: currentUser });
});

// Get all books
app.get('/api/books', (req, res) => {
  const books = library.getAllBooks();
  res.json({ books, currentUser });
});

// Borrow a book
app.post('/api/books/borrow', (req, res) => {
  if (!currentUser) {
    return res.json({ success: false, message: 'Not logged in' });
  }

  const { bookTitle } = req.body;
  const result = library.borrowBook(currentUser, bookTitle);
  res.json(result);
});

// Return a book
app.post('/api/books/return', (req, res) => {
  if (!currentUser) {
    return res.json({ success: false, message: 'Not logged in' });
  }

  const { bookTitle } = req.body;
  const result = library.returnBook(currentUser, bookTitle);
  res.json(result);
});

// Place hold on a book
app.post('/api/books/hold', (req, res) => {
  if (!currentUser) {
    return res.json({ success: false, message: 'Not logged in' });
  }

  const { bookTitle } = req.body;
  const result = library.placeHold(currentUser, bookTitle);
  res.json(result);
});

// Get borrowed books for current user
app.get('/api/borrowed-books', (req, res) => {
  if (!currentUser) {
    return res.json({ books: [] });
  }

  const books = library.getBorrowedBooks(currentUser);
  res.json({ books });
});

// Get notifications for current user
app.get('/api/notifications', (req, res) => {
  if (!currentUser) {
    return res.json({ notifications: [] });
  }

  const notifications = library.getNotifications(currentUser);
  res.json({ notifications });
});

// Clear notifications for current user
app.post('/api/notifications/clear', (req, res) => {
  if (!currentUser) {
    return res.json({ success: false });
  }

  library.clearAllNotifications(currentUser);
  res.json({ success: true });
});

// Reset endpoint (REQUIRED for Cypress tests)
app.post('/api/reset', (req, res) => {
  library.reset();
  currentUser = null;
  res.json({ success: true, message: 'Library reset to initial state' });
});

// ============================================
// SERVE HTML PAGES
// ============================================

// Root path serves login page
app.get('/', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'login.html'));
});

// Library dashboard (requires login)
app.get('/library', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

// Redirect index.html to root (prevents direct access)
app.get('/index.html', (req, res) => {
  res.redirect('/library');
});

// ============================================
// START SERVER
// ============================================

app.listen(PORT, () => {
  console.log(`Library Management System running at http://localhost:${PORT}`);
  console.log('Press Ctrl+C to stop the server');
});