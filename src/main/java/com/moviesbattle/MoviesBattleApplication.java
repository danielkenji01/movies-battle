package com.moviesbattle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
//@EnableJpaRepositories(basePackages = "com.moviesbattle.repository")
@EnableFeignClients
public class MoviesBattleApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoviesBattleApplication.class, args);
	}

}
