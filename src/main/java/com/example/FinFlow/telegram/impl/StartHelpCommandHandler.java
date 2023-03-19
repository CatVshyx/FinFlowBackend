package com.example.FinFlow.telegram.impl;

import com.example.FinFlow.telegram.TelegramConfig;
import com.example.FinFlow.telegram.KeyboardHelper;
import com.example.FinFlow.telegram.UserRequest;
import com.example.FinFlow.telegram.UserRequestHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Component
public class StartHelpCommandHandler extends UserRequestHandler {

    private static String command = "/start";

    public StartHelpCommandHandler(TelegramConfig telegramConfig, KeyboardHelper keyboardHelper) {
        super(telegramConfig, keyboardHelper);
    }


    @Override
    public void handle(UserRequest userRequest) {
        ReplyKeyboardMarkup replyKeyboard = keyboardHelper.buildMainMenu();

        telegramConfig.sendMessage(userRequest.userId(),
                "Hello! You can easily help people of FinanceFlow with their ordinary problems just with your phone",replyKeyboard);

    }

    @Override
    public boolean isApplicable(UserRequest userRequest) {
        return UserRequestHandler.isCommand(userRequest.textFromUser(),command);
    }
}
