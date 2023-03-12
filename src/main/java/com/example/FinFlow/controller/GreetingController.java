package com.example.FinFlow.controller;

import com.example.FinFlow.additional.Response;
import com.example.FinFlow.config.JwtService;
import com.example.FinFlow.model.Company;
import com.example.FinFlow.model.Date;
import com.example.FinFlow.model.Product;
import com.example.FinFlow.model.User;
import com.example.FinFlow.repository.CompanyRepository;
import com.example.FinFlow.repository.DateRepository;
import com.example.FinFlow.repository.ProductRepository;
import com.example.FinFlow.service.CompanyService;
import com.example.FinFlow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.Random;

@RestController
public class GreetingController {
    @Autowired
    private UserService userService;
    @Autowired
    private DateRepository dateRepository;
    @Autowired
    private CompanyRepository companyRepositary;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private CompanyService companyService;
//    @GetMapping("/")
//    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name) {
//        return "Hello %s".formatted(name);
//    }

    @GetMapping("/addTestDates")
    public ResponseEntity<String> addNewDatesTEST(@RequestParam(name = "amount") int amount){
        if(amount == 0) return new ResponseEntity<>("amount can`t be 0", HttpStatus.BAD_REQUEST);
        Random rd = new Random();
        for(int i = 0; i < amount; i++){
            Date local = new Date(LocalDate.now().minusDays(i),rd.nextLong(10000));
            dateRepository.save(local);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/addTestDatesToCompany")
    public ResponseEntity<String> addTestDatesToCompanyTEST(@RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        Random rd = new Random();
        User user = userService.findByEmail(getEmailByToken(token)).orElse(null);
        Company company = companyService.findCompanyById(user.getCompany().intValue());
        if (company == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        for(int i = 0; i < 70; i++){
            Date local = new Date(LocalDate.now().minusDays(i),rd.nextLong(10000));
            dateRepository.save(local);
            company.getRevenues().add(local);
        }

        companyRepositary.save(company);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    private  String getEmailByToken(String token){
        final String jwtToken = token.substring(7);
        return jwtService.extractEmail(jwtToken);
    }

    @GetMapping("/addTestSoldProperty")
    private ResponseEntity<Object> addTestSoldProperty(){
        Company[] companies = companyService.getAllExistingCompanies();
        Random rd = new Random();

        for (Company currCompany : companies){
            currCompany.getProducts()
                    .forEach(product -> product.setSold(rd.nextInt(100)));
            companyService.updateCompany(currCompany);
        }
        return new ResponseEntity<>("Test sold has been added to all the products ", HttpStatusCode.valueOf(200));
    }

}