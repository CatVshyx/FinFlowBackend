package com.example.FinFlow.telegram;

public record UserRequest(Long userId, String userFirstName, String textFromUser) {
}
