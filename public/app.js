// Library Dashboard JavaScript

let currentUser = null;
let books = [];
let borrowedBooks = [];
let notifications = [];

// ============================================
// INITIALIZATION
// ============================================

document.addEventListener('DOMContentLoaded', async () => {
  await checkAuth();
  setupEventListeners();
  await loadData();
});

// Check if user is authenticated
async function checkAuth() {
  try {
    const response = await fetch('/api/current-user');
    const data = await response.json();

    if (!data.loggedIn) {
      // Not logged in - redirect to login page once
      if (window.location.pathname !== '/') {
        window.location.replace('/');
      }
      return;
    }

    currentUser = data.username;
    const userElement = document.getElementById('current-user');
    if (userElement) {
      userElement.textContent = `Welcome, ${currentUser}!`;
    }
  } catch (error) {
    console.error('Auth check failed:', error);
    // Only redirect if not already on login page
    if (window.location.pathname !== '/') {
      window.location.replace('/');
    }
  }
}

// ============================================
// EVENT LISTENERS
// ============================================

function setupEventListeners() {
  // Logout button
  document.getElementById('logout-btn').addEventListener('click', logout);

  // Navigation buttons
  document.querySelectorAll('.nav-btn').forEach(btn => {
    btn.addEventListener('click', (e) => {
      const view = e.currentTarget.dataset.view;
      switchView(view);
    });
  });
}

// ============================================
// NAVIGATION
// ============================================

function switchView(viewName) {
  // Update active nav button
  document.querySelectorAll('.nav-btn').forEach(btn => {
    btn.classList.remove('active');
  });
  document.querySelector(`[data-view="${viewName}"]`).classList.add('active');

  // Update active view
  document.querySelectorAll('.view').forEach(view => {
    view.classList.remove('active');
  });
  document.getElementById(`${viewName}-view`).classList.add('active');

  // Load data for the view
  if (viewName === 'all-books') {
    loadBooks();
  } else if (viewName === 'my-books') {
    loadBorrowedBooks();
  } else if (viewName === 'notifications') {
    loadNotifications();
  }
}

// ============================================
// DATA LOADING
// ============================================

async function loadData() {
  await loadBooks();
  await loadBorrowedBooks();
  await loadNotifications();
}

async function loadBooks() {
  try {
    const response = await fetch('/api/books');
    const data = await response.json();
    books = data.books;
    renderBooks();
  } catch (error) {
    showMessage('Failed to load books', 'error');
  }
}

async function loadBorrowedBooks() {
  try {
    const response = await fetch('/api/borrowed-books');
    const data = await response.json();
    borrowedBooks = data.books;
    updateBorrowedCount();
    renderBorrowedBooks();
  } catch (error) {
    showMessage('Failed to load borrowed books', 'error');
  }
}

async function loadNotifications() {
  try {
    const response = await fetch('/api/notifications');
    const data = await response.json();
    notifications = data.notifications;
    updateNotificationBadge();
    renderNotifications();
  } catch (error) {
    showMessage('Failed to load notifications', 'error');
  }
}

// ============================================
// RENDERING
// ============================================

function renderBooks() {
  const container = document.getElementById('books-list');

  if (books.length === 0) {
    container.innerHTML = '<div class="empty-state"><div class="empty-state-icon">📚</div><p>No books available</p></div>';
    return;
  }

  container.innerHTML = books.map(book => {
    const isBorrowed = borrowedBooks.some(b => b.title === book.title);

    // Can borrow if:
    // 1. Book status is "Available" and not already borrowed and under limit
    // 2. Book is "On Hold" for current user and not already borrowed and under limit
    const isReservedForMe = book.onHoldBy === currentUser;
    const canBorrow = ((book.status === 'Available' || isReservedForMe) && !isBorrowed && borrowedBooks.length < 3);
    const canReturn = isBorrowed;
    const canHold = book.status !== 'Available' && !isBorrowed && !isReservedForMe;

    const statusClass = book.status === 'Available' ? 'status-available' :
                       book.status === 'Checked Out' ? 'status-checked-out' :
                       'status-on-hold';

    return `
      <div class="book-card" data-testid="book-card-${book.title.replace(/\s+/g, '-').toLowerCase()}">
        <div class="book-title" data-testid="book-title">${book.title}</div>
        <div class="book-author" data-testid="book-author">${book.author}</div>
        <span class="book-status ${statusClass}" data-testid="book-status">${book.status}</span>
        ${isReservedForMe ? '<div style="color: #667eea; font-weight: 600; font-size: 13px; margin-top: 8px;">Reserved for you</div>' : ''}
        ${book.queueLength > 0 ? `<div style="color: #666; font-size: 13px; margin-top: 5px;">${book.queueLength} person(s) in queue</div>` : ''}
        <div class="book-actions">
          <button
            class="btn btn-primary"
            data-testid="borrow-btn-${book.title.replace(/\s+/g, '-').toLowerCase()}"
            onclick="borrowBook('${book.title}')"
            ${!canBorrow ? 'disabled' : ''}
          >
            Borrow
          </button>
          <button
            class="btn btn-hold"
            data-testid="hold-btn-${book.title.replace(/\s+/g, '-').toLowerCase()}"
            onclick="placeHold('${book.title}')"
            ${!canHold ? 'disabled' : ''}
          >
            Place Hold
          </button>
          <button
            class="btn btn-return"
            data-testid="return-btn-${book.title.replace(/\s+/g, '-').toLowerCase()}"
            onclick="returnBook('${book.title}')"
            ${!canReturn ? 'disabled' : ''}
          >
            Return
          </button>
        </div>
      </div>
    `;
  }).join('');
}

function renderBorrowedBooks() {
  const container = document.getElementById('borrowed-books-list');

  if (borrowedBooks.length === 0) {
    container.innerHTML = '<div class="empty-state"><div class="empty-state-icon">📖</div><p>You have not borrowed any books</p></div>';
    return;
  }

  container.innerHTML = borrowedBooks.map(book => `
    <div class="borrowed-item" data-testid="borrowed-book-${book.title.replace(/\s+/g, '-').toLowerCase()}">
      <div class="borrowed-info">
        <h3>${book.title}</h3>
        <p>${book.author}</p>
        <p class="due-date" data-testid="due-date">Due: ${book.dueDate}</p>
      </div>
      <button
        class="btn btn-return"
        onclick="returnBook('${book.title}')"
        data-testid="return-borrowed-btn-${book.title.replace(/\s+/g, '-').toLowerCase()}"
      >
        Return Book
      </button>
    </div>
  `).join('');
}

function renderNotifications() {
  const container = document.getElementById('notifications-list');

  if (notifications.length === 0) {
    container.innerHTML = '<div class="empty-state"><div class="empty-state-icon">🔔</div><p>No new notifications</p></div>';
    return;
  }

  container.innerHTML = notifications.map((notif, index) => `
    <div class="notification-item" data-testid="notification-${index}">
      <div class="notification-icon">📖</div>
      <div class="notification-content">
        <p data-testid="notification-message">${notif.message}</p>
        <span class="notification-time">${new Date(notif.timestamp).toLocaleString()}</span>
      </div>
    </div>
  `).join('');
}

// ============================================
// UPDATE UI ELEMENTS
// ============================================

function updateBorrowedCount() {
  const countElement = document.getElementById('borrowed-count');
  countElement.textContent = `${borrowedBooks.length}/3`;

  if (borrowedBooks.length >= 3) {
    countElement.style.color = '#dc3545';
  } else {
    countElement.style.color = '#667eea';
  }
}

function updateNotificationBadge() {
  const badge = document.getElementById('notification-badge');
  if (notifications.length > 0) {
    badge.textContent = notifications.length;
    badge.style.display = 'inline-block';
  } else {
    badge.style.display = 'none';
  }
}

// ============================================
// BOOK ACTIONS
// ============================================

async function borrowBook(bookTitle) {
  try {
    const response = await fetch('/api/books/borrow', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ bookTitle })
    });

    const result = await response.json();

    if (result.success) {
      showMessage(result.message, 'success');
      await loadData();
    } else {
      showMessage(result.message, 'error');
    }
  } catch (error) {
    showMessage('Failed to borrow book', 'error');
  }
}

async function returnBook(bookTitle) {
  try {
    const response = await fetch('/api/books/return', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ bookTitle })
    });

    const result = await response.json();

    if (result.success) {
      showMessage(result.message, 'success');
      await loadData();
    } else {
      showMessage(result.message, 'error');
    }
  } catch (error) {
    showMessage('Failed to return book', 'error');
  }
}

async function placeHold(bookTitle) {
  try {
    const response = await fetch('/api/books/hold', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ bookTitle })
    });

    const result = await response.json();

    if (result.success) {
      showMessage(result.message, 'success');
      await loadData();
    } else {
      showMessage(result.message, 'error');
    }
  } catch (error) {
    showMessage('Failed to place hold', 'error');
  }
}

// ============================================
// AUTHENTICATION
// ============================================

async function logout() {
  try {
    await fetch('/api/logout', { method: 'POST' });
    window.location.href = '/';
  } catch (error) {
    showMessage('Logout failed', 'error');
  }
}

// ============================================
// UI HELPERS
// ============================================

function showMessage(message, type = 'success') {
  const container = document.getElementById('message-container');
  const messageDiv = document.createElement('div');
  messageDiv.className = `message ${type}`;
  messageDiv.textContent = message;
  messageDiv.setAttribute('data-testid', `message-${type}`);

  container.appendChild(messageDiv);

  setTimeout(() => {
    messageDiv.remove();
  }, 5000);
}