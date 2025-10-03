package com.SpringBoot.EventServices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
// want automatic stable serialization everywhere
public class EventServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventServicesApplication.class, args);
	}

}
