package com.example.myrealog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MyrealogApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyrealogApplication.class, args);
	}

}
