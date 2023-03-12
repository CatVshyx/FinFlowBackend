package com.example.FinFlow;

import com.example.FinFlow.controller.GreetingController;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
class FinFlowApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private GreetingController greetingController;

//	@Test
//	void shouldReturnAllProducts() throws Exception {
//		this.mockMvc.perform(get("/getAllProducts"))
//				.andDo(print())
//				.andExpect(status().isOk())
//				.andExpect(content().string(notNullValue()));
//
//	}
//	@Test
//	void shouldReturnHello() throws Exception{
//		this.mockMvc.perform(get("/"))
//				.andDo(print())
//				.andExpect(status().isOk());
//	}
}
