package com.github.randdd32.donor_search_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DonorSearchBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DonorSearchBackendApplication.class, args);
	}

}
