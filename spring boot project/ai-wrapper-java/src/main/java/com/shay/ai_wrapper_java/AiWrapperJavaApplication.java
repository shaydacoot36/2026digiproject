package com.shay.ai_wrapper_java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.shay.ai_wrapper_java", "com.example.aiplanner"})
@EntityScan(basePackages = {"com.shay.ai_wrapper_java.model", "com.example.aiplanner.model"})
@EnableJpaRepositories(basePackages = {"com.shay.ai_wrapper_java.repository", "com.example.aiplanner.repository"})
public class AiWrapperJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiWrapperJavaApplication.class, args);
	}

}