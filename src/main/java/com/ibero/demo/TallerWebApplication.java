package com.ibero.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TallerWebApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(TallerWebApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	}

}
