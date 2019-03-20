package com.spgcld.demo.seqid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient //标识具体的服务,需要向注册中心注册
@SpringBootApplication
public class Application {
			
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
