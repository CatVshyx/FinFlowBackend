package com.example.FinFlow.telegram;

import com.example.FinFlow.telegram.handler.UserRequestHandler;
import com.example.FinFlow.telegram.model.UserRequest;

import java.util.List;

public class TelegramDispatcher {
    // dispatch what handler to give back with command
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
