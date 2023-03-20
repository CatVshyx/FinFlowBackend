package com.example.FinFlow.telegram;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@Component
public class KeyboardHelper {
    public ReplyKeyboardMarkup buildMenu(String info) {
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(info);
        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(keyboardRow))
                .selective(true)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
    }
    public ReplyKeyboardMarkup buildWorkMenu(){
        List<KeyboardButton> buttons = List.of(new KeyboardButton("Next \uD83D\uDC49"), new KeyboardButton("Choose ✅"));

        KeyboardRow row1 = new KeyboardRow(buttons);
        KeyboardRow row2 = new KeyboardRow(List.of(new KeyboardButton("Exit \uD83D\uDEAA")));

        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row1,row2))
                .selective(true)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
    }
//    public ReplyKeyboardMarkup buildMenuWithCancel() {
//        KeyboardRow keyboardRow = new KeyboardRow();
//        keyboardRow.add("Cancel ❌");
//
//        return ReplyKeyboardMarkup.builder()
//                .keyboard(List.of(keyboardRow))
//                .selective(true)
//                .resizeKeyboard(true)
//                .oneTimeKeyboard(false)
//                .build();
//    }
//    public ReplyKeyboardMarkup buildMenuWithConfirm() {
//        KeyboardRow keyboardRow = new KeyboardRow();
//        keyboardRow.add("Approve ✅");
//
//        return ReplyKeyboardMarkup.builder()
//                .keyboard(List.of(keyboardRow))
//                .selective(true)
//                .resizeKeyboard(true)
//                .oneTimeKeyboard(false)
//                .build();
//    }
}
