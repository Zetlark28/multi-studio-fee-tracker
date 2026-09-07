package com.zetlark.multistudiofeetrackerbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MultiStudioFeeTrackerBeApplication {

	static void main(String[] args) {
		SpringApplication.run(MultiStudioFeeTrackerBeApplication.class, args);
	}

}
