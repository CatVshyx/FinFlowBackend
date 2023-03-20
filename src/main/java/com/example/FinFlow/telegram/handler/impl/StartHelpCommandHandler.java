package com.example.FinFlow.telegram.handler.impl;

import com.example.FinFlow.telegram.TelegramBot;
import com.example.FinFlow.telegram.handler.UserRequestHandler;
import com.example.FinFlow.telegram.KeyboardHelper;
import com.example.FinFlow.telegram.model.UserRequest;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Component
public class StartHelpCommandHandler extends UserRequestHandler {

    private static String[] command = {"/start","/help"};

    public StartHelpCommandHandler(TelegramBot telegramConfig, KeyboardHelper keyboardHelper) {
        super(telegramConfig, keyboardHelper);
    }
    @Override
    public void handle(UserRequest userRequest) {
        ReplyKeyboardMarkup replyKeyboard = keyboardHelper.buildMenu("Start help ❗");
        boolean chosenCommand = userRequest.getText().equals("/start");
        String response = chosenCommand
                ? "Hello! You can easily help people of FinanceFlow with their ordinary problems just with your phone"
                : "Follow all the requests, but you need access_token to work with bot";
        telegramConfig.sendMessage(userRequest.getUserSession().getChatId(), response,replyKeyboard);

    }

    @Override
    public boolean isApplicable(UserRequest userRequest) {
        String text = userRequest.getText();
        return UserRequestHandler.isCommand(text,command[0]) || UserRequestHandler.isCommand(text,command[1]);
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
