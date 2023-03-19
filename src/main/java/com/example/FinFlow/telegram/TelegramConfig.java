package com.example.FinFlow.telegram;

import com.example.FinFlow.telegram.impl.EnterUUIDHandler;
import com.example.FinFlow.telegram.impl.StartHelpCommandHandler;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;

@Service
public class TelegramConfig extends TelegramLongPollingBot {
    private  TelegramDispatcher dispatcher;
    private KeyboardHelper keyboardHelper = new KeyboardHelper();
    public TelegramConfig() throws TelegramApiException {
        System.out.println("Bot initializing ");
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(this);
        List<UserRequestHandler> handlerList = new ArrayList<>();
        handlerList.add(new StartHelpCommandHandler(this,keyboardHelper));
        handlerList.add(new EnterUUIDHandler(this,keyboardHelper));
        dispatcher = new TelegramDispatcher(handlerList);

    }
    @Override
    public String getBotUsername() {
        return "Finance2003Bot";
    }

    @Override
    public String getBotToken() {
        return "5955538348:AAFEP8LKpRglV0JzDAtKq00mJpQPxfheN3k";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String textFromUser = update.getMessage().getText();

            Long userId = update.getMessage().getChatId();
            String userFirstName = update.getMessage().getFrom().getFirstName();

            System.out.println("[{%s}, {%s}] : {%s}".formatted( userId, userFirstName, textFromUser));
            UserRequest request = new UserRequest(userId,userFirstName,textFromUser);
            if (!dispatcher.dispatch(request)){
                sendMessage(userId,"Not realized");
            }
//            SendMessage sendMessage = SendMessage.builder()
//                    .chatId(userId.toString())
//                    .text("Hello, I've received your text: " + textFromUser)
//                    .build();
//            try {
//                this.sendApiMethod(sendMessage);
//            } catch (TelegramApiException e) {
//                e.printStackTrace();
//            }
        } else {
           System.out.println("Unexpected update from user");
        }
    }
    public void sendMessage(Long chatId, String message){
        try {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(message)
                    .build();
            this.sendApiMethod(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendMessage(Long chatId, String message, ReplyKeyboardMarkup markup){
        try {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId.toString())
                    .replyMarkup(markup)
                    .text(message)
                    .build();
            this.sendApiMethod(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }
}
