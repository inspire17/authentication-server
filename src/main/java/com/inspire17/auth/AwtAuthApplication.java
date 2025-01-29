package com.inspire17.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
@ComponentScan(basePackages = "com.inspire17.auth")
public class AwtAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(AwtAuthApplication.class, args);
	}

}
