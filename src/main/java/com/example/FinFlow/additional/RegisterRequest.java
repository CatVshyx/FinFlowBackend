package com.example.FinFlow.additional;

public class RegisterRequest {
    private String email;
    private String username;
    private String password;
    private String number;
    public RegisterRequest(String email, String username, String password,String phoneNumber) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.number = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

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

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", number='" + number + '\'' +
                '}';
    }
}
