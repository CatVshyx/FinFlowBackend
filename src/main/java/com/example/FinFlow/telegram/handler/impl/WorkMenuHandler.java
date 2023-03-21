package com.example.FinFlow.telegram.handler.impl;

import com.example.FinFlow.telegram.ConversationState;
import com.example.FinFlow.telegram.KeyboardHelper;
import com.example.FinFlow.telegram.TelegramBot;
import com.example.FinFlow.telegram.handler.UserRequestHandler;
import com.example.FinFlow.telegram.model.ClientHelp;
import com.example.FinFlow.telegram.model.UserRequest;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorkMenuHandler extends UserRequestHandler {
    private List<String> commands = new ArrayList<>(Arrays.asList("/workMenu","Next \uD83D\uDC49","Exit \uD83D\uDEAA","Choose ✅"));
    private boolean sayHi = true;
    public WorkMenuHandler(TelegramBot telegramConfig, KeyboardHelper keyboardHelper) {
        super(telegramConfig, keyboardHelper);

    }

    @Override
    public void handle(UserRequest userRequest) {
        String chosenCommand = userRequest.getText();
        Long id = userRequest.getUserSession().getChatId();
        int index = commands.indexOf(chosenCommand);
        switch (index){
            case 0:
                ReplyKeyboardMarkup replyKeyboardMarkup = keyboardHelper.buildWorkMenu();
                if (sayHi){
                    telegramConfig.sendMessage(id,"Hello %s, it`s nice to see you again".formatted(userRequest.getUsername()));
                    sayHi = false;
                }

                telegramConfig.sendMessage(id,nextHelp(),replyKeyboardMarkup);
                break;
            case 1:
                telegramConfig.sendMessage(id,nextHelp());
                break;
            case 2:
                userRequest.getUserSession().setState(ConversationState.CONVERSATION_STARTED);
                userRequest.setText("Exit \uD83D\uDEAA");
                telegramConfig.dispatch(userRequest);
                break;
            case 3:
                userRequest.setText("/sendMenu");
                telegramConfig.dispatch(userRequest);
                break;
        }
    }
    public void deleteEmail(){}
    public String nextHelp(){
        ClientHelp help = telegramConfig.getRequestList().nextHelp();
        return "------------------\nTitle:%s\nemail:%s\nrequest:%s".formatted(help.title(),help.email(),help.description());
    }
    @Override
    public boolean isApplicable(UserRequest userRequest) {
        return userRequest.getUserSession().getState().equals(ConversationState.CODE_APPLIED) && commands.contains(userRequest.getText());
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
