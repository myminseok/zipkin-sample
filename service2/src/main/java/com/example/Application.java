package com.example;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
@EnableCircuitBreaker
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Bean
	@LoadBalanced
	RestTemplate restTemplate(){ return new RestTemplate();}

	@Autowired
	RestTemplate restTemplate;


	@RequestMapping("/call")
	public String callMethod() throws InterruptedException{
		log.info("from service2, calling service3");
		String service3 = restTemplate.getForObject("http://service3/call",String.class);
		String service4 = restTemplate.getForObject("http://service4/call",String.class);
		return service3+service4;

	}

	@RequestMapping("/call-timeout")
	public String callTimeout() throws InterruptedException{
		log.info("from service2, calling service3");
		String response = restTemplate.getForObject("http://service3/call-timeout",String.class);
		Thread.sleep(100);
		log.info("Got response [{}]", response);
		return response;
	}

	@Autowired
	FallbackTest fallbackTest;
	@RequestMapping("/fallbacktest")
	public String method2() throws InterruptedException {
		return fallbackTest.service2_OK_Method();
	}
}

