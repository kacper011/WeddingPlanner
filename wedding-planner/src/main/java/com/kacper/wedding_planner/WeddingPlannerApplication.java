package com.kacper.wedding_planner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class WeddingPlannerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeddingPlannerApplication.class, args);
	}

}
