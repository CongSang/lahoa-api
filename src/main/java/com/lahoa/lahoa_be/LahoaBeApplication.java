package com.lahoa.lahoa_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class LahoaBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(LahoaBeApplication.class, args);
	}

}
