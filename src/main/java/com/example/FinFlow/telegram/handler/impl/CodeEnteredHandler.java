package com.example.FinFlow.telegram.handler.impl;

import com.example.FinFlow.telegram.ConversationState;
import com.example.FinFlow.telegram.KeyboardHelper;
import com.example.FinFlow.telegram.TelegramBot;
import com.example.FinFlow.telegram.handler.UserRequestHandler;
import com.example.FinFlow.telegram.model.UserRequest;

public class CodeEnteredHandler extends UserRequestHandler {

    private String appCode = "12345";
    public CodeEnteredHandler(TelegramBot telegramConfig, KeyboardHelper keyboardHelper) {
        super(telegramConfig, keyboardHelper);
    }
    @Override
    public void handle(UserRequest userRequest) {
        String code = userRequest.getText();
        boolean isApplied = code.equals(appCode);

//        userRequest.getUserSession().setState(isApplied ? ConversationState.CODE_APPLIED : ConversationState.CODE_DENIED);
        if(isApplied){
            userRequest.getUserSession().setState(ConversationState.CODE_APPLIED);
            userRequest.setText("/workMenu");
            telegramConfig.dispatch(userRequest);
        }else {
            telegramConfig.sendMessage(userRequest.getUserSession().getChatId(), "Please enter your code again");
        }
    }
    @Override
    public boolean isApplicable(UserRequest userRequest) {
        return userRequest.getUserSession().getState().equals(ConversationState.WAITING_FOR_CODE);
    }
    @Override
    public boolean isGlobal() {
        return false;
    }
}
