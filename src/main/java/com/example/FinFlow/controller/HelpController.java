package com.example.FinFlow.controller;

import com.example.FinFlow.config.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/help")
public class HelpController {
    @Autowired
    private JwtService jwtService;
    private final File f = new File("src/main/resources/static/help.txt");
    @PostMapping("/sendQuestion")
    public ResponseEntity<String> sendHelp(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody Map<String,String> request){
        // request TITLE - DESCRIBE
        try(FileWriter writer = new FileWriter(f, true)) {
            if (request.get("title") == null || request.get("description") == null) return new ResponseEntity<>("Please, request must have title and description", HttpStatusCode.valueOf(400));
            String text = "-----------------------------"+'\n';

            writer.write(text);
            writer.write("Email:"+getEmailByToken(token)+'\n');
            writer.write("Title:"+request.get("title")+'\n');
            writer.write("Description:"+request.get("description")+'\n');

            writer.flush();
        }
        catch(IOException ex){
            ex.printStackTrace();
            return new ResponseEntity<>("Something went wrong", HttpStatusCode.valueOf(500));
        }
        return new ResponseEntity<>("Your request has been added, we will answer you soon", HttpStatus.OK);
    }

    private  String getEmailByToken(String token){
        final String jwtToken = token.substring(7);
        return jwtService.extractEmail(jwtToken);
    }
}
