package com.example.FinFlow.controller;

import com.example.FinFlow.additional.AuthenticationResponse;
import com.example.FinFlow.additional.RegisterRequest;
import com.example.FinFlow.additional.Response;
import com.example.FinFlow.config.JwtService;
import com.example.FinFlow.driveAPI.DriveService;
import com.example.FinFlow.model.Company;
import com.example.FinFlow.model.Date;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import java.io.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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


    // TEST
    @PostMapping("/uploadPhoto")
    public ResponseEntity<Object> uploadPhoto(@RequestParam(name = "file") MultipartFile file) throws IOException {
        Response response = driveService.uploadFile(file.getOriginalFilename(),file.getInputStream(),DriveService.PHOTO_FOLDER);
        return new ResponseEntity<>(response.getDescription(), response.getHttpCode());
    }
    @PostMapping("/deleteFile")
    public ResponseEntity<Object> deleteFile(@RequestParam(name = "id") String id) {
        Response response = driveService.deleteFile(id);
        return new ResponseEntity<>(response.getDescription(), response.getHttpCode());
    }
    @PostMapping("/createFolder")
    public ResponseEntity<Object> createFolder(@RequestParam(name = "name") String name) {
        Response response = driveService.createFolder(name);
        return new ResponseEntity<>(response.getDescription(), response.getHttpCode());
    }
    @PostMapping("/updateFile")
    public ResponseEntity<Object> updateFile(@RequestParam(name = "name") String name,@RequestParam("file") MultipartFile file) throws IOException {
        Response response = driveService.updateFile(name,file.getInputStream());
        return new ResponseEntity<>(response.getDescription(), response.getHttpCode());
    }
    @GetMapping("/redirectURLwithPrefix")
    public ModelAndView redirectWithUsingRedirectView(ModelMap attributes){

        attributes.addAttribute("attribute","redirectURLwithPrefix");
        return new ModelAndView("forward:/auth/",attributes); // forward does not send 302(all the stuff is done on the server side without and att) redirect does
    }

    @GetMapping("/addTestDates")
    public ResponseEntity<String> addNewDatesTEST(@RequestParam(name = "company_id") int id){
        Company company = companyService.findCompanyById(id);
        Set<Date> revs = company.getRevenues();
        if (revs.size() > 0){
            revs.clear();
        }
        Random rd = new Random();
        for(int i = 0; i < 30; i++){
            Date local = new Date(LocalDate.now().minusDays(i),rd.nextLong(10000));
            revs.add(local);
        }
        company.setRevenues(revs);
        companyService.updateCompany(company);
        return new ResponseEntity<>("Test dates were added",HttpStatus.OK);
    }
    @GetMapping("/deleteTestDates")
    public ResponseEntity<String> removeTestDates(@RequestParam(name = "company_id") int id){
        Company comp = companyService.findCompanyById(id);
        if (comp == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        comp.getRevenues().clear();
        companyService.updateCompany(comp);
        return new ResponseEntity<>("Test dates were deleted",HttpStatus.OK);
    }
}
