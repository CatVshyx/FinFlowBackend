package com.example.FinFlow;

import com.example.FinFlow.model.Category;
import com.example.FinFlow.controller.GreetingController;


import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Random;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@WithMockUser(username = "JOHN", authorities = { "SYS_ADMIN" })
public class ProductApplicationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GreetingController greetingController;

//    @Test
//    public void shouldReturnAllProducts() throws Exception {
//        this.mockMvc.perform(get("/getAllProducts"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().string(notNullValue()));
//    }
//
//    @Test
//    public void shouldAddProduct() throws Exception{
//        Random rd = new Random();
//
//        JSONObject object = new JSONObject();
//        object.put("name","nmee"+rd.nextInt(200));
//        object.put("price", String.valueOf(rd.nextInt(250)));
//        object.put("amount", String.valueOf(rd.nextInt(250)));
//        object.put("category", Category.values()[rd.nextInt(Category.values().length)].name());
//        // FOR SENDING JSON set content type
//        this.mockMvc.perform(post("/res/addProduct").content(object.toString() ) .contentType(APPLICATION_JSON))
//                .andDo(print()).andExpect(status().isOk());
//
//    }
//    @Test
//    public void shouldNotAddProduct() throws Exception{
//        Random rd = new Random();
//
//        JSONObject object = new JSONObject();
//        object.put("name","nmee"+rd.nextInt(200));
//        object.put("amount", String.valueOf(rd.nextInt(250)));
//        object.put("category", Category.values()[rd.nextInt(Category.values().length)].name());
//        // FOR SENDING JSON set content type
//        this.mockMvc.perform(post("/res/addProduct").content(object.toString() ) .contentType(APPLICATION_JSON))
//                .andDo(print()).andExpect(status().isBadRequest());
//    }
//    @Test
//    public void shouldNotAddProductBecauseExists() throws Exception{
//        Random rd = new Random();
//
//        JSONObject object = new JSONObject();
//        object.put("name","Banana");
//        object.put("price", String.valueOf(rd.nextInt(250)));
//        object.put("amount", String.valueOf(rd.nextInt(250)));
//        object.put("category", Category.values()[rd.nextInt(Category.values().length)].name());
//        // FOR SENDING JSON set content type
//        this.mockMvc.perform(post("/res/addProduct").content(object.toString() ) .contentType(APPLICATION_JSON))
//                .andDo(print()).andExpect(status().isBadRequest());
//    }
//    @Test
//    public void shouldDeleteProduct() throws Exception{
//        JSONObject object = new JSONObject();
//        object.put("id","302");
//
//        // FOR SENDING JSON set content type
//        this.mockMvc.perform(post("/res/deleteProduct").content(object.toString() ) .contentType(APPLICATION_JSON))
//                .andDo(print()).andExpect(status().isOk());
//    }
//    @Test
//    public void shouldNotDeleteProduct() throws Exception{
//        JSONObject object = new JSONObject();
//        object.put("id","11111");
//
//        // FOR SENDING JSON set content type
//        this.mockMvc.perform(post("/res/deleteProduct").content(object.toString() ) .contentType(APPLICATION_JSON))
//                .andDo(print()).andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void shouldEditProductName() throws Exception{
//        JSONObject object = new JSONObject();
//        object.put("id","252");
//        object.put("name","Apricot");
//        // FOR SENDING JSON set content type
//        this.mockMvc.perform(post("/res/editProduct").content(object.toString() ) .contentType(APPLICATION_JSON))
//                .andDo(print()).andExpect(status().isOk());
//    }
//    @Test
//    public void shouldNotEditProductName() throws Exception{
//        JSONObject object = new JSONObject();
//        object.put("id","2222");
//        object.put("name","Apricot");
//        // FOR SENDING JSON set content type
//        this.mockMvc.perform(post("/res/editProduct").content(object.toString() ) .contentType(APPLICATION_JSON))
//                .andDo(print()).andExpect(status().isBadRequest());
//    }
//    @Test
//    public void shouldAddDiscount() throws Exception{
//        JSONObject object = new JSONObject();
//        object.put("id","252");
//        object.put("discount","0.23");
//        object.put("start_date", LocalDate.now());
//        object.put("end_date",LocalDate.now().plusDays(15));
//
//        // FOR SENDING JSON set content type
//        this.mockMvc.perform(post("/res/addDiscount").content(object.toString() ) .contentType(APPLICATION_JSON))
//                .andDo(print()).andExpect(status().isOk());
//    }
//    @Test
//    public void shouldChangeExistingDiscount() throws Exception{
//        JSONObject object = new JSONObject();
//        object.put("id","252");
//        object.put("discount","0.67");
//
//
//        // FOR SENDING JSON set content type
//        this.mockMvc.perform(post("/res/editDiscount").content(object.toString() ) .contentType(APPLICATION_JSON))
//                .andDo(print()).andExpect(status().isOk());
//    }
//    @Test
//    public void shouldNotChangeDiscount() throws Exception{
//        JSONObject object = new JSONObject();
//        object.put("id","202");
//        object.put("discount","0.67");
//
//
//        // FOR SENDING JSON set content type
//        this.mockMvc.perform(post("/res/editDiscount").content(object.toString() ) .contentType(APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//
//    }
//    @Test
//    public void shouldDeleteDiscount() throws Exception{
//        JSONObject object = new JSONObject();
//        object.put("id","252");
//        // FOR SENDING JSON set content type
//        this.mockMvc.perform(post("/res/deleteDiscount").content(object.toString() ) .contentType(APPLICATION_JSON))
//                .andDo(print()).andExpect(status().isOk());
//    }
//
//    @Test
//    public void shouldAddNewDates() throws Exception{
//        this.mockMvc.perform(post("/addTestDates")
//                .param("amount", String.valueOf(11)))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//    @Test
//    public void shouldAddNewDatesToCompany() throws Exception{
//        JSONObject object = new JSONObject();
//        object.put("id","802");
//        this.mockMvc.perform(post("/addTestDatesToCompany")
//                        .param("amount", String.valueOf(11)).content(object.toString()).contentType(APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
}
