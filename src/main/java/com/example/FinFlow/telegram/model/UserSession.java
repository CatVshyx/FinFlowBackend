package com.example.FinFlow.telegram.model;

import com.example.FinFlow.telegram.ConversationState;

public class UserSession {
    private final Long chatId;
    private ConversationState state;
    public UserSession(Long chatId) {
        this.chatId = chatId;
    }

    public Long getChatId() {
        return chatId;
    }

    public ConversationState getState() {
        return state;
    }

    public UserSession setState(ConversationState state) {
        this.state = state;
        return this;
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "chatId=" + chatId +
                ", state=" + state +
                '}';
    }
}
