package com.aether.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class AetherVideoEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(AetherVideoEngineApplication.class, args);
		log.info("******************Aether Video Engine Service started successfully******************");
	}

}
