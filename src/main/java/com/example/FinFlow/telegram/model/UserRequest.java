package com.example.FinFlow.telegram.model;

public class UserRequest{
    private String username;
    private String text;
    private UserSession userSession;


    public UserRequest(String userFirstName, String textFromUser, UserSession userSession){
        this.username = userFirstName;
        this.userSession = userSession;
        this.text = textFromUser;
    }
    public String getUsername() {
        return username;
    }
    public String getText() {
        return text;
    }
    public UserSession getUserSession() {
        return userSession;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "UserRequest{" +
                "username='" + username + '\'' +
                ", text='" + text + '\'' +
                ", userSession=" + userSession +
                '}';
    }
}
