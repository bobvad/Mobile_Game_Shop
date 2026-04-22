package com.example.mobile_gamestoreshop.models;

public class User {
    private int id;
    private String login;
    private String email;
    private String password;
    private String role;
    private String dateTimeCreated;
    private boolean isGuest;

    public User() {}

    public User(String login, String email, String password) {
        this.login = login;
        this.email = email;
        this.password = password;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getDateTimeCreated() { return dateTimeCreated; }
    public void setDateTimeCreated(String dateTimeCreated) { this.dateTimeCreated = dateTimeCreated; }

    public boolean isGuest() { return isGuest; }
    public void setGuest(boolean guest) { isGuest = guest; }
}