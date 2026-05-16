package com.student.studentgradesystem;

public class User {

    private String username;
    private String password;
    private String role; // "ADMIN" or "TEACHER"

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getRole()     { return role; }

    // Check if password matches
    public boolean checkPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }
}