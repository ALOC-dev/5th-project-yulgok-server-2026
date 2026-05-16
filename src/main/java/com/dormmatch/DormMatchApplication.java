package com.dormmatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class})
public class DormMatchApplication {

	public static void main(String[] args) {

		SpringApplication.run(DormMatchApplication.class, args);
	}

}
