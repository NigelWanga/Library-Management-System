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
    if (window.location.pathname !== '/') {
      window.location.replace('/');
    }
  }
}

// ============================================
// EVENT LISTENERS
// ============================================

function setupEventListeners() {
  document.getElementById('logout-btn').addEventListener('click', logout);

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
  document.querySelectorAll('.nav-btn').forEach(btn => {
    btn.classList.remove('active');
  });
  document.querySelector(`[data-view="${viewName}"]`).classList.add('active');

  document.querySelectorAll('.view').forEach(view => {
    view.classList.remove('active');
  });
  document.getElementById(`${viewName}-view`).classList.add('active');

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
  // Load borrowed books first so renderBooks() has accurate data
  await loadBorrowedBooks();
  await loadBooks();
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

  // Create a Set of borrowed book titles for fast lookup
  const borrowedTitles = new Set(borrowedBooks.map(b => b.title));

  container.innerHTML = books.map(book => {
    const isBorrowedByMe = borrowedTitles.has(book.title);
    const isReservedForMe = book.onHoldBy === currentUser;

    // Button logic - allow clicking borrow even at limit so server can return error message
    const canBorrow = (book.status === 'Available' || isReservedForMe) && !isBorrowedByMe;
    const canReturn = isBorrowedByMe;
    const canHold = book.status === 'Checked Out' && !isBorrowedByMe && !isReservedForMe;

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
            onclick="borrowBook('${book.title.replace(/'/g, "\\'")}')"
            ${!canBorrow ? 'disabled' : ''}
          >
            Borrow
          </button>
          <button
            class="btn btn-hold"
            data-testid="hold-btn-${book.title.replace(/\s+/g, '-').toLowerCase()}"
            onclick="placeHold('${book.title.replace(/'/g, "\\'")}')"
            ${!canHold ? 'disabled' : ''}
          >
            Place Hold
          </button>
          <button
            class="btn btn-return"
            data-testid="return-btn-${book.title.replace(/\s+/g, '-').toLowerCase()}"
            onclick="returnBook('${book.title.replace(/'/g, "\\'")}')"
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
        onclick="returnBook('${book.title.replace(/'/g, "\\'")}')"
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