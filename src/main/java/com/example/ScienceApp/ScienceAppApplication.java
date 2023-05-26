package com.example.ScienceApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({ "com.example.ScienceApp.*" })
public class ScienceAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScienceAppApplication.class, args);
	}

}
