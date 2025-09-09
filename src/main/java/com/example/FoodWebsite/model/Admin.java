package com.example.FoodWebsite.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Admin {

    @Id
    private String username = "admin"; // Hardcoded username

    private String password = "admin123"; // Hardcoded password

    // Constructors
    public Admin() {
    }

    public Admin(String password) {
        this.password = password; // Allow form to set password for comparison
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}