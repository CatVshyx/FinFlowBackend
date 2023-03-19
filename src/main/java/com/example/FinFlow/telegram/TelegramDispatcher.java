package com.example.FinFlow.telegram;

import java.util.List;

public class TelegramDispatcher {
    private final List<UserRequestHandler> handlers;

    public TelegramDispatcher(List<UserRequestHandler> handlers){
        this.handlers = handlers;
    }
    public boolean dispatch(UserRequest userRequest){
        for (UserRequestHandler handler : handlers){
            if(handler.isApplicable(userRequest)){
                handler.handle(userRequest);
                return true;
            }
        }
        return false;
    }
}
