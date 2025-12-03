// Custom Cypress commands for Library Management System

// Login command (if needed for future enhancements)
Cypress.Commands.add('login', (username, password) => {
  cy.visit('http://localhost:3000');
  cy.get('[data-testid="username-input"]').type(username);
  cy.get('[data-testid="password-input"]').type(password);
  cy.get('[data-testid="login-btn"]').click();
});

// Logout command
Cypress.Commands.add('logout', () => {
  cy.get('[data-testid="logout-btn"]').click();
});

// Add more custom commands here if needed
// Note: If you add assertions inside commands, make sure to document them
// as per the assignment requirements