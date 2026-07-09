package com.dormmatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DormMatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(DormMatchApplication.class, args);
	}

}
