package com.example.FinFlow.telegram.impl;

import com.example.FinFlow.telegram.TelegramConfig;
import com.example.FinFlow.telegram.KeyboardHelper;
import com.example.FinFlow.telegram.UserRequest;
import com.example.FinFlow.telegram.UserRequestHandler;

public class EnterUUIDHandler extends UserRequestHandler {
    private final String command = "Help users❗️";
    private String pass = "12345";
    public EnterUUIDHandler(TelegramConfig telegramConfig, KeyboardHelper keyboardHelper) {
        super(telegramConfig, keyboardHelper);
    }

    @Override
    public void handle(UserRequest userRequest) {
        String text = userRequest.textFromUser();

        telegramConfig.sendMessage(userRequest.userId(),text.equals(pass) ? "Successfully saved" : "no");
    }

    @Override
    public boolean isApplicable(UserRequest userRequest) {
        return isCommand(userRequest.textFromUser(),command);
    }
}
