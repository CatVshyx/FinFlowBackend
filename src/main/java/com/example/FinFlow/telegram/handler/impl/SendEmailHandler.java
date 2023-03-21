package com.example.FinFlow.telegram.handler.impl;

import com.example.FinFlow.telegram.ConversationState;
import com.example.FinFlow.telegram.KeyboardHelper;
import com.example.FinFlow.telegram.TelegramBot;
import com.example.FinFlow.telegram.handler.UserRequestHandler;
import com.example.FinFlow.telegram.model.UserRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SendEmailHandler extends UserRequestHandler {
    private List<String> commands = new ArrayList<>(Arrays.asList("/sendMenu","Send \uD83D\uDCE7","Cancel ❌"));
    private String message;
    public SendEmailHandler(TelegramBot telegramConfig, KeyboardHelper keyboardHelper) {
        super(telegramConfig, keyboardHelper);

    }

    @Override
    public void handle(UserRequest userRequest) {
        int val = commands.indexOf(userRequest.getText());

        switch (val){
            case 0:
                telegramConfig.sendMessage(userRequest.getUserSession().getChatId(),"Please, write your message",keyboardHelper.buildSendMenu());
                userRequest.getUserSession().setState(ConversationState.WAITING_FOR_MESSAGE);
                break;
            case 1:
                if (message == null || message.length() < 4){
                    telegramConfig.sendMessage(userRequest.getUserSession().getChatId(),"Please write message");
                    return;
                }
                telegramConfig.sendMail(telegramConfig.getRequestList().getChosenClient().email(),message);
                telegramConfig.sendMessage(userRequest.getUserSession().getChatId(),"Message was sent " + message);
                returnToWorkMenu(userRequest);
                break;
            case 2:
                returnToWorkMenu(userRequest);
                break;
            default:
                message = userRequest.getText();
        }
    }
    private void returnToWorkMenu(UserRequest userRequest){
        userRequest.setText("/workMenu");
        userRequest.getUserSession().setState(ConversationState.CODE_APPLIED);
        telegramConfig.dispatch(userRequest);
    }
    @Override
    public boolean isApplicable(UserRequest userRequest) {
        return userRequest.getUserSession().getState().equals(ConversationState.CODE_APPLIED) && commands.contains(userRequest.getText())
                || userRequest.getUserSession().getState().equals(ConversationState.WAITING_FOR_MESSAGE);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
