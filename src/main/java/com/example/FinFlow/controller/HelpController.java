package com.example.FinFlow.controller;

import com.example.FinFlow.config.JwtService;
import com.example.FinFlow.driveAPI.DriveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.util.ArrayUtils;

import java.io.*;
import java.util.Map;

@Controller
@RequestMapping("/help")
public class HelpController {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private DriveService service;
    @PostMapping("/sendQuestion")
    public ResponseEntity<String> sendHelp(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody Map<String,String> request) throws IOException {
        Map.Entry<InputStream,String> entry = service.downloadFileAsInputStream(DriveService.HELP_ID);

        StringBuilder builder = new StringBuilder("\n");
        builder.append("Email:%s".formatted(getEmailByToken(token)));
        builder.append("|Title:%s".formatted(request.get("title")));
        builder.append("|Message:%s".formatted(request.get("description")));

        byte[] byteRequest = builder.toString().getBytes();
        byte[] bytes = entry.getKey().readAllBytes();
        byte[] mainArray = new byte[byteRequest.length + bytes.length];
        System.arraycopy(byteRequest,0,mainArray,0,byteRequest.length);
        System.arraycopy(bytes,0,mainArray,byteRequest.length,bytes.length);

        ByteArrayInputStream bais = new ByteArrayInputStream(mainArray);

        service.updateFile(DriveService.HELP_ID,bais);

        return new ResponseEntity<>("Your request has been added, we will answer you soon", HttpStatus.OK);
    }

    private  String getEmailByToken(String token){
        final String jwtToken = token.substring(7);
        return jwtService.extractEmail(jwtToken);
    }
}
