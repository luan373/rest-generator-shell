package br.com.nicolae.restgenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
public class RestGeneratorShellApplication {

	static void main(String[] args) {
		SpringApplication.run(RestGeneratorShellApplication.class, args);
	}

}
