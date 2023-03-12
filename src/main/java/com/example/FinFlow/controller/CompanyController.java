package com.example.FinFlow.controller;

import com.example.FinFlow.additional.Response;
import com.example.FinFlow.config.JwtService;
import com.example.FinFlow.model.User;
import com.example.FinFlow.service.CompanyService;
import com.example.FinFlow.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Controller
@RequestMapping("/company")
public class CompanyController {
    @Autowired
    private CompanyService companyService;
    @Autowired
    private UserService service;
    @Autowired
    private ResourceLoader resourceLoader;
    @Value("${upload.path}")
    private String uploadPath;
    @Autowired
    private JwtService jwtService;
    @PostMapping("/createCompany")
    public ResponseEntity<String> createCompany(@RequestHeader(HttpHeaders.AUTHORIZATION) String token , @RequestBody Map<String,String> request){
        if(!request.containsKey("name")) return new ResponseEntity<>("Write the company name", HttpStatus.BAD_REQUEST);

        Response isCreated = companyService.createCompany(request.get("name"), getEmailByToken(token));
        return new ResponseEntity<>(isCreated.getDescription().toString(), isCreated.getHttpCode());
    }
    @PreAuthorize("hasAuthority('admin_edit')")
    @GetMapping("/removeCompany")
    public ResponseEntity<String> removeCompany(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token){
        Response ans = companyService.removeCompany(getEmailByToken(token));
        return new ResponseEntity<>(ans.getDescription().toString(), ans.getHttpCode());
    }
    @PostMapping("/joinByCode")
    public ResponseEntity<String> addUserByCode(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody Map<String,String> request){
        // user which got this code must be registered and send this code
        if(!request.containsKey("code")) return new ResponseEntity<>("To add the user to your company, write company code",HttpStatus.BAD_REQUEST);

        Response ans  = companyService.addUserByCode(getEmailByToken(token),request.get("code"));
        return new ResponseEntity<>(ans.getDescription().toString(), ans.getHttpCode());
    }
    @PreAuthorize("hasAuthority('admin_edit')")
    @DeleteMapping("/deleteUser")
    public ResponseEntity<String> deleteUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String token ,@RequestBody Map<String,String> request){
        if(!request.containsKey("email")) return new ResponseEntity<>("To delete the user from your company, write user`s email",HttpStatus.BAD_REQUEST);
        Response ans = companyService.deleteUser(getEmailByToken(token),request.get("email"));

        System.out.println(ans.getDescription());
        return new ResponseEntity<>(ans.getDescription().toString(), ans.getHttpCode());
    }
    @PreAuthorize("hasAuthority('admin_edit')")
    @PostMapping("/changeAllows")
    public ResponseEntity<String> changeRoles(@RequestHeader(HttpHeaders.AUTHORIZATION) String token ,@RequestBody Map<String,String> request){
        if(!request.containsKey("email")) return new ResponseEntity<>("To delete the user from your company, write user`s email",HttpStatus.BAD_REQUEST);
        Response ans  = companyService.changeUserAllows(getEmailByToken(token),request);

        return new ResponseEntity<>(ans.getDescription().toString(), ans.getHttpCode());
    }
    @PreAuthorize("hasAnyAuthority('admin_edit')")
    @PostMapping("/inviteBySpecification")
    public ResponseEntity<String> addUserBySpecification(@RequestHeader(HttpHeaders.AUTHORIZATION) String token ,@RequestBody Map<String,Object> request){
        if(!request.containsKey("email")) return new ResponseEntity<>("To change the user in your company, write user`s email",HttpStatus.BAD_REQUEST);
        if(request.size() == 1) return new ResponseEntity<>("The request has denied because there is no change, to change user properties , write like storage_edit:true ",HttpStatus.BAD_REQUEST);

        Response ans = companyService.addUserBySpecification(getEmailByToken(token),request);

        return new ResponseEntity<>(ans.getDescription().toString(), ans.getHttpCode());
    }
    @GetMapping("/getData")
    public ResponseEntity<String> getData(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) throws JsonProcessingException {

        Response response = companyService.getAllCompanyData(getEmailByToken(token));
        return new ResponseEntity<>(response.getDescription().toString(), response.getHttpCode());
    }
    @GetMapping("/loadUserImage")
    public ResponseEntity<Resource> uploadUserImage(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam(name = "email") String email) throws MalformedURLException {
        Response response = service.getUsersProfile(getEmailByToken(token),email);
        if (response.getHttpCode() != HttpStatus.OK){
            return new ResponseEntity<>(null,response.getHttpCode());
        }

        File f = (File) response.getDescription();
        Path path = Paths.get(f.toURI());
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }




    private  String getEmailByToken(String token){
        final String jwtToken = token.substring(7);
        return jwtService.extractEmail(jwtToken);
    }
    @GetMapping("/leaveCompany")
    public Object leave(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, ModelMap map){
        Response response = companyService.leaveCompany(getEmailByToken(token));
        if (response.getHttpCode().value() == 302){
            map.addAttribute("attribute","leaveCompany");
            System.out.println("redirection");
            return new ModelAndView("forward:/company/removeCompany",map);
        }
        return new ResponseEntity<>(response.getDescription().toString(), response.getHttpCode());
    }
}
