package com.example.FinFlow.controller;

import com.example.FinFlow.additional.AuthenticationResponse;
import com.example.FinFlow.additional.RegisterRequest;
import com.example.FinFlow.additional.Response;
import com.example.FinFlow.config.JwtService;
import com.example.FinFlow.driveAPI.DriveService;
import com.example.FinFlow.model.User;
import com.example.FinFlow.service.AuthenticationService;
import com.example.FinFlow.service.CompanyService;
import com.example.FinFlow.service.EmailService;
import com.example.FinFlow.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import java.io.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserService userService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private DriveService driveService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterRequest request){
        Response response = authenticationService.register(request);

        return new ResponseEntity<>(response.getDescription().toString(), response.getHttpCode());
    }
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody RegisterRequest request){
        if (request.getEmail() == null || request.getPassword() == null) return new ResponseEntity<>("Email and password fields are not filled",HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok( authenticationService.login(request));}
    @GetMapping("/activate/{code}")
    public ResponseEntity<String> activate(@PathVariable String code){
        boolean isActivated = authenticationService.activate(code);
        if (isActivated) {return new ResponseEntity<>("Activated", HttpStatus.OK);}
        return new ResponseEntity<>("User not found",HttpStatus.OK);

    }
    @GetMapping("/addUserBySpecification/{code}")
    public ResponseEntity<String> addUserConfirm(@PathVariable String code){
        Response ans = companyService.confirmAddingBySpecification(code);
        return new ResponseEntity<>(ans.getDescription().toString(), ans.getHttpCode());
    }
    @PostMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String,String> request){
        if(!request.containsKey("email")) return new ResponseEntity<>("set the field 'email' for us to send you code", HttpStatus.BAD_REQUEST);
        Response response = userService.setForgotCode(request.get("email"));
        if(response.getHttpCode().value() != 200) return new ResponseEntity<>(response.getDescription().toString(), response.getHttpCode());

        Runnable runnable = () -> emailService.sendEmail(request.get("email"),"Forgot Password","Your code:"+response.getDescription().toString());
        Thread thread = new Thread(runnable,"emailSend");
        thread.start();

        return new ResponseEntity<>("The code was successfully sent on your email", HttpStatusCode.valueOf(200));
    }
    @PostMapping("/resetPassword")
    public ResponseEntity<Object> resetPassword( @RequestBody Map<String,String> password){
        if(!password.containsKey("code") || !password.containsKey("password"))
            return new ResponseEntity<>("Either code is not written or your new password is not set",HttpStatus.BAD_REQUEST);

        Response response = userService.changePasswordByCode(password);

        if (response.getHttpCode() != HttpStatus.OK) return new ResponseEntity<>(response.getDescription().toString(),HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>("Success",response.getHttpCode());
    }
    @GetMapping("/info")
    public ResponseEntity<StreamingResponseBody> infoAPI(){
        HttpHeaders headers = new HttpHeaders();
        Map.Entry<InputStream,String> entry = driveService.downloadFileAsInputStream(DriveService.INFO_ID);
        headers.setContentType(MediaType.parseMediaType(entry.getValue()));

        StreamingResponseBody body = DriveService.getStreamResponseBody(entry.getKey());
        return ResponseEntity.ok().headers(headers).body(body);
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<Object> refreshToken(HttpServletRequest request, HttpServletResponse response){
        final String authHeader =  request.getHeader("Authorization");

        final String userEmail;
        final String refreshToken;
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            return new ResponseEntity<>("Missing refresh token",HttpStatus.BAD_REQUEST);
        }

        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractEmail(refreshToken);
        User user =userService.findByEmail(userEmail).orElse(null);
        if(user == null) return new ResponseEntity<>("User not found", HttpStatusCode.valueOf(404));

        String access = jwtService.generateToken(User.toUserDetails(user),3);

        AuthenticationResponse response1 = new AuthenticationResponse(access,refreshToken);

        return new ResponseEntity<>(response1, HttpStatus.OK);


    }
    @GetMapping("/")
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name) {
        System.out.println("hello from attribute");
        return "Hello %s".formatted(name);
    }
    @GetMapping("/media/{id}")
    public ResponseEntity<StreamingResponseBody> uploadMedia(@PathVariable("id") String fileId){
        HttpHeaders headers = new HttpHeaders();
        Map.Entry<InputStream,String> entry = driveService.downloadFileAsInputStream(fileId);
        headers.setContentType(MediaType.parseMediaType(entry.getValue()));
        StreamingResponseBody body = DriveService.getStreamResponseBody(entry.getKey());

        return ResponseEntity.ok().headers(headers).body(body);

    }
    @GetMapping("/redirectURLwithPrefix")
    public ModelAndView redirectWithUsingRedirectView(ModelMap attributes){

        attributes.addAttribute("attribute","redirectURLwithPrefix");
        return new ModelAndView("forward:/auth/",attributes); // forward does not send 302(all the stuff is done on the server side without and att) redirect does
    }
}
