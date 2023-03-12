package com.example.FinFlow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;


@SpringBootApplication
@Configuration
//@EnableScheduling
public class FinFlowApplication {
	// TODO deploy app
	public static void main(String[] args) {
		SpringApplication.run(FinFlowApplication.class, args);
	}

}
