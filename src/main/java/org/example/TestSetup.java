package org.example;

public class TestSetup {
    private final Catalogue catalogue;
    private final BorrowerRegistry registry;
    private final Authenticator authSystem;
    private Borrower currentUser;


    public TestSetup(String username, String password) { //for our resp tests
        InitializeLibrary library = new InitializeLibrary();
        this.catalogue = library.initializeLibrary();

        InitializeBorrowers initborrowers = new InitializeBorrowers();
        this.registry = initborrowers.initializeBorrowers();

        this.authSystem = new Authenticator(registry);

        boolean isLoggedIn = authSystem.login(username, password);
        this.currentUser = authSystem.getCurrentUser();

        assert isLoggedIn : "Login failed for " + username;
        assert currentUser != null : "No active user for " + username;
    }

    public Catalogue getCatalogue() { return catalogue; }
    public BorrowerRegistry getRegistry() { return registry; }
    public Authenticator getAuthSystem() { return authSystem; }
    public Borrower getCurrentUser() { return currentUser; }

}
