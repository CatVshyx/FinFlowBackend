package com.example.FinFlow.telegram.handler.impl;

import com.example.FinFlow.telegram.ConversationState;
import com.example.FinFlow.telegram.TelegramBot;
import com.example.FinFlow.telegram.UserSessionService;
import com.example.FinFlow.telegram.handler.UserRequestHandler;
import com.example.FinFlow.telegram.KeyboardHelper;
import com.example.FinFlow.telegram.model.UserSession;
import com.example.FinFlow.telegram.model.UserRequest;

public class EnterUUIDHandler extends UserRequestHandler {
    private final String command = "Start help ❗";
    private final UserSessionService sessionService;
    public EnterUUIDHandler(TelegramBot telegramConfig, KeyboardHelper keyboardHelper, UserSessionService userSessionService) {
        super(telegramConfig, keyboardHelper);
        this.sessionService = userSessionService;
    }

    @Override
    public void handle(UserRequest userRequest) {
        UserSession session = userRequest.getUserSession();
        session.setState(ConversationState.WAITING_FOR_CODE);
        sessionService.saveSession(session.getChatId(),session);

        telegramConfig.sendMessage(userRequest.getUserSession().getChatId(),"Please enter your Unique code");
    }

    @Override
    public boolean isApplicable(UserRequest userRequest) {
        return isCommand(userRequest.getText(),command);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
