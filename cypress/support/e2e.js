// Import commands
import './commands';

// Prevent Cypress from failing tests on uncaught exceptions
// (useful for handling expected errors in the application)
Cypress.on('uncaught:exception', (err, runnable) => {
  // Return false to prevent the test from failing
  // Only do this for expected errors in your app
  return false;
});