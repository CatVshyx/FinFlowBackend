package com.example.FinFlow.telegram.handler;

import com.example.FinFlow.telegram.TelegramBot;
import com.example.FinFlow.telegram.KeyboardHelper;
import com.example.FinFlow.telegram.model.UserRequest;

public abstract class UserRequestHandler {
    protected TelegramBot telegramConfig;

    protected KeyboardHelper keyboardHelper;
    public UserRequestHandler(TelegramBot telegramConfig, KeyboardHelper keyboardHelper) {
        this.telegramConfig = telegramConfig;
        this.keyboardHelper = keyboardHelper;
    }
    public abstract void handle(UserRequest userRequest);

    public abstract boolean isApplicable(UserRequest userRequest);

    public abstract boolean isGlobal();
    protected static boolean isCommand(String userUpdate, String handlersCommand){
        return handlersCommand.equals(userUpdate);
    }
}
