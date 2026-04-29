package com.shay.ai_wrapper_java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.aiplanner.repository")
public class AiWrapperJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiWrapperJavaApplication.class, args);
	}

}
