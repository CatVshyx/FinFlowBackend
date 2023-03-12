package com.example.FinFlow.controller;

import com.example.FinFlow.additional.AuthenticationResponse;
import com.example.FinFlow.additional.RegisterRequest;
import com.example.FinFlow.additional.Response;
import com.example.FinFlow.config.JwtService;
import com.example.FinFlow.model.User;
import com.example.FinFlow.service.AuthenticationService;
import com.example.FinFlow.service.CompanyService;
import com.example.FinFlow.service.EmailService;
import com.example.FinFlow.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    @Value("${upload.path}")
    private String uploadPath;
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
    private UserDetailsService userDetailsService;

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

        // sends code to the email specified
        // user gets this code and writes it
        //
        return new ResponseEntity<>("The code was successfully sent on your email", HttpStatusCode.valueOf(200));
    }
    @PostMapping("/resetPassword")
    public ResponseEntity<Object> resetPassword( @RequestBody Map<String,String> password){
        // code password
        // he resets password by code - code becomes null, password is changed and i give him jwt token
        if(!password.containsKey("code") || !password.containsKey("password")) return new ResponseEntity<>("Either code is not written or your new password is not set",HttpStatus.BAD_REQUEST);

        Response response = userService.changePasswordByCode(password);

        if (response.getHttpCode() != HttpStatus.OK) return new ResponseEntity<>(response.getDescription().toString(),HttpStatus.BAD_REQUEST);
//        Map<String,String> data = ((Map<String, String>) response.getDescription());

//        AuthenticationResponse auth = authenticationService.login(new RegisterRequest(data.get("email"), null,data.get("password"),null));
//        System.out.println(auth.toString());
        return new ResponseEntity<>("Success",response.getHttpCode());
    }
    @GetMapping("/info")
    public ResponseEntity<String> infoAPI(){
        StringBuilder builder = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/static/info.txt"))){
            while (reader.ready()){
                String str = reader.readLine();
                builder.append(str).append('\n');
            }
            return new ResponseEntity<>(builder.toString(), HttpStatusCode.valueOf(200));
        }catch (IOException e){
            throw new RuntimeException(e);
        }
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

        String access = jwtService.generateToken(User.toUserDetails(user),2);

        AuthenticationResponse response1 = new AuthenticationResponse(access,refreshToken);

        return new ResponseEntity<>(response1, HttpStatus.OK);


    }
    @GetMapping("/")
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, @RequestParam(name = "attribute",required = false, defaultValue = "not_set")String attribute) {
        System.out.println("hello from attribute " + attribute );
        return "Hello %s".formatted(name);
    }
    @GetMapping("/redirectURLwithPrefix")
    public ModelAndView redirectWithUsingRedirectView(ModelMap attributes){

        attributes.addAttribute("attribute","redirectURLwithPrefix");
        return new ModelAndView("forward:/auth/",attributes); // forward does not send 302(all the stuff is done on the server side without and att) redirect does
    }
//    @GetMapping("/redirectURL")
//    public RedirectView redirectWithUsingRedirectView(RedirectAttributes attributes){
//        attributes.addAttribute("attribute","redirectUrl");
//        return new RedirectView("/auth/"); // sends 302
//    }
}
