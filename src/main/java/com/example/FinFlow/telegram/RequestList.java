package com.example.FinFlow.telegram;

import com.example.FinFlow.telegram.model.ClientHelp;

import java.util.ArrayList;
import java.util.List;

public class RequestList {
    List<ClientHelp> list = new ArrayList<>();

    public ClientHelp nextHelp(){
        return list.get(0);
    }
    public boolean deleteRequest(ClientHelp help){
        return list.remove(help);
    }
    public void uploadRequests(){
        return;
    }
}
