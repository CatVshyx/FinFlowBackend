package com.example.FinFlow;

import com.example.FinFlow.controller.UserController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class UserTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserController userController;
//    @Test
//    public void shouldGetAllUsers() throws Exception {
//        this.mockMvc
//                .perform(get("/getAllUsers"))
//                .andDo(print()).andExpect(status().isOk());
//    }
//    @Test
//    public void shouldAddNewUsers() throws Exception {
//        this.mockMvc
//                .perform(get("/addNewUsers").param("amount","15"))
//                .andDo(print()).andExpect(status().isOk());
//    }
}
