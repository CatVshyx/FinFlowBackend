package com.example.FinFlow.telegram;

import com.example.FinFlow.telegram.model.ClientHelp;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Component
public class RequestList {
    List<ClientHelp> list = new ArrayList<>(Arrays.asList(
            new ClientHelp("title","dmitroshapiro@gmail.com","desc"),
            new ClientHelp("title","igor@gmail.com","desc"),
            new ClientHelp("title","vasyl@gmail.com","desc")));
    int queue = 0;
    public RequestList(){
        for(int i = 0; i < 5; i++){
            list.add(new ClientHelp("title"+i,"dmitroshapiro"+i+"@gmail.com","description"));
        }
        System.out.println("Request list initialized");
    }

    private ClientHelp chosenClient;

    public ClientHelp nextHelp(){
        chosenClient = list.get(queue%list.size());
        queue++;
        return chosenClient;
    }
    public boolean deleteRequest(ClientHelp help){
        return list.remove(help);
    }
    public void uploadRequests(){
        return;
    }

    public ClientHelp getChosenClient() {
        return chosenClient;
    }
}
