package com.example.FinFlow;

import com.example.FinFlow.config.JwtService;
import com.example.FinFlow.controller.CompanyController;
import com.example.FinFlow.service.UserService;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Random;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@WithMockUser(username = "cvkvitu@gmail.com",password = "12345",roles = {"OWNER"})
public class CompanyTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CompanyController controller;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
//    @Test
//    public void shouldCreateCompany() throws Exception {
//        JSONObject object = new JSONObject();
//        Random rd = new Random();
//        object.put("id","12");
//        object.put("name","Ellestro"+ rd.nextInt(20));
//
//
//        // FOR SENDING JSON set content type
//        this.mockMvc.perform(post("/company/createCompany").content(object.toString() ) .contentType(APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//    @Test
//
//    public void shouldntCreateCompanyUserDoesntExist(){}
//    @Test
//
//    public void shouldDeleteCompany() throws Exception{
//
//        // FOR SENDING JSON set content type
//        this.mockMvc.perform(post("/company/removeCompany"))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//    @Test
//    public void shouldAddUserToCompany(){}
}
