package com.github.jatinde.webflux_payground;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.h2.H2ConsoleAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(value={H2ConsoleAutoConfiguration.class})
public class WebfluxPaygroundApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebfluxPaygroundApplication.class, args);
	}

}
