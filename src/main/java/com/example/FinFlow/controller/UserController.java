package com.example.FinFlow.controller;

import com.example.FinFlow.additional.AuthenticationResponse;
import com.example.FinFlow.additional.RegisterRequest;
import com.example.FinFlow.additional.Response;
import com.example.FinFlow.config.JwtService;
import com.example.FinFlow.model.User;
import com.example.FinFlow.service.AuthenticationService;
import com.example.FinFlow.service.FileService;
import com.example.FinFlow.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;


    @Autowired
    private JwtService jwtService;

    @Autowired
    private FileService fileService;

    @Autowired
    private AuthenticationService authenticationService;
    @GetMapping("/getAllUsers")
    public ResponseEntity<Iterable<User>> getAllUsersTEST(){
        return new ResponseEntity<>(userService.findAllUsers(), HttpStatus.OK);
    }
    @GetMapping("/addNewUsers")
    public ResponseEntity<String> addNewUsersTEST(@RequestParam(name = "amount") int amount){
        boolean isAdded = userService.addTestUsers(amount);
        if (isAdded) return new ResponseEntity<>("added",HttpStatus.OK);
        return new ResponseEntity<>("problem",HttpStatus.BAD_REQUEST);
    }
    @PostMapping("/changeUserSettings")
    public ResponseEntity<String> changeUserSettings(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,@RequestBody(required = false) Map<String,String> request,  @RequestParam(name = "image",required = false) MultipartFile  file) {
        //can change phonenumber,name,
        String email = getEmailByToken(token);
        Response response = new Response("changed",200);
        if (request != null){ response = userService.changeUsersSettings(email,request); }
        return new ResponseEntity<>(response.getDescription().toString(), response.getHttpCode());
    }
    @PostMapping("/uploadUserPhoto")
    public ResponseEntity<Object> uploadPhoto(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam(name = "file") MultipartFile  file){
        Response response;
        System.out.println("file is null" + file.isEmpty());
        try{
            response = fileService.uploadFile(getEmailByToken(token),file);
        }catch (IOException e){
            return new ResponseEntity<>("Something went wrong during uploading your photo, please try it again",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response.getDescription(), response.getHttpCode());
    }
    @GetMapping("/getMe")
    public ResponseEntity<Object> getMe(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) throws JsonProcessingException {
        User user = userService.findByEmail(getEmailByToken(token)).orElse(null);
        if (user == null) return new ResponseEntity<>("User not found",HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(new ObjectMapper().writeValueAsString(user),HttpStatus.OK);

    }
    @PostMapping("/changePassword")
    public ResponseEntity<Object> changePassword(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody Map<String,String> request){
        if(!request.containsKey("new_password") || !request.containsKey("current_password") ) return new ResponseEntity<>("Password to change and current password are not set",HttpStatus.BAD_REQUEST);

        Response response = userService.changePassword(getEmailByToken(token),request);
        if (response.getHttpCode().value() != 200) return new ResponseEntity<>(response.getDescription().toString(), response.getHttpCode());

        Map<String,String> data = ((Map<String, String>) response.getDescription());
        AuthenticationResponse auth = authenticationService.login(new RegisterRequest(data.get("email"), null,data.get("password"),null));
        return new ResponseEntity<>(auth, response.getHttpCode());

    }
    private  String getEmailByToken(String token){
        final String jwtToken = token.substring(7);

        return jwtService.extractEmail(jwtToken);
    }
}
