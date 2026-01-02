# Library Management System
This is a full-stack application built using Java, JavaScript, and Cypress, following BDD (Behavior-Driven Development) and TDD (Test-Driven Development) principles.

The system manages library operations, including borrowing and returning books, user role management, and availability tracking, while ensuring correctness through automated unit and end-to-end tests.

## Tech Stack
Backend: Java

Frontend/UI: JavaScript, HTML, CSS

Testing:

JUnit (unit tests)

Cypress (end-to-end tests)

Methodology: BDD + TDD

## Branch Overview
1. main -	Stable, production-ready version
2. bdd-tdd-core -	Domain logic and unit tests (TDD-driven)
3. e2e-cypress -	UI and end-to-end tests implemented with Cypress

## Setup Instructions
1. Install dependencies: npm install


## Running the Application
1. Start the server: npm start
2. Open browser: http://localhost:3000


## Running Cypress Tests

### Headless Mode
npm run cypress:run

### Interactive Mode
npx cypress open
