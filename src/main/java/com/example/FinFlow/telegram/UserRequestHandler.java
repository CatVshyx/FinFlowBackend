package com.example.FinFlow.telegram;

public abstract class UserRequestHandler {
    protected TelegramConfig telegramConfig;

    protected KeyboardHelper keyboardHelper;
    public UserRequestHandler(TelegramConfig telegramConfig, KeyboardHelper keyboardHelper) {
        this.telegramConfig = telegramConfig;
        this.keyboardHelper = keyboardHelper;
    }
    public void handle(UserRequest userRequest) {}

    public boolean isApplicable(UserRequest userRequest) {
        return false;
    }

    protected static boolean isCommand(String userUpdate, String handlersCommand){
        return handlersCommand.equals(userUpdate);
    }
}
