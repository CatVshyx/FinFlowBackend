package com.example.FinFlow.controller;

import com.example.FinFlow.additional.AuthenticationResponse;
import com.example.FinFlow.additional.RegisterRequest;
import com.example.FinFlow.additional.Response;
import com.example.FinFlow.config.JwtService;
import com.example.FinFlow.driveAPI.DriveService;
import com.example.FinFlow.model.User;
import com.example.FinFlow.service.AuthenticationService;
import com.example.FinFlow.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
    private DriveService driveService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationService authenticationService;
    @PostMapping("/changeUserSettings")
    public ResponseEntity<String> changeUserSettings(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,@RequestBody(required = false) Map<String,String> request) {
        String email = getEmailByToken(token);
        Response response = new Response("changed",200);
        if (request != null){ response = userService.changeUsersSettings(email,request); }
        return new ResponseEntity<>(response.getDescription().toString(), response.getHttpCode());
    }
    @PostMapping("/uploadPhoto")
    public ResponseEntity<Object> uploadPhoto(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam(name = "file") MultipartFile  file) throws IOException {
        String email = getEmailByToken(token);
        User user = userService.findByEmail(email).orElse(null);
        if (user == null){
            return new ResponseEntity<>("Something went wrong during uploading your photo, please try it again",HttpStatus.BAD_REQUEST);
        }
        Response response;
        if (user.getPhotos() == null){
            response = driveService.uploadFile(file.getOriginalFilename(),file.getInputStream(),DriveService.PHOTO_FOLDER);
            user.setPhotos(response.getDescription().toString());
            userService.saveUser(user);
        }else{
            response = driveService.updateFile(user.getPhotos(),file.getInputStream());
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
