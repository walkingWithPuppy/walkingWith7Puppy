package com.turkey.walkingwith7puppy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class WalkingWith7PuppyApplication {

	public static void main(String[] args) {
		SpringApplication.run(WalkingWith7PuppyApplication.class, args);
	}

}
