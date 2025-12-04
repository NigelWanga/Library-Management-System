// Custom Cypress commands for Library Management System

// Login command
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
