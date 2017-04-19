package com.newegg.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@ComponentScan("com.newegg.redis.**")
@EnableWebSocket
public class ServiceStarter {

	public static void main(String[] args) {
		SpringApplication.run(ServiceStarter.class, args);
	}
}
