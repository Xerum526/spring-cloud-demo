package com.spgcld.demo.webfront;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.ribbon.RibbonClient;

//开启代理配置
@EnableFeignClients
//开启断路器配置
@EnableCircuitBreaker
//标识具体的服务,需要向注册中心注册
@EnableDiscoveryClient
//自定义负载均衡策略
@RibbonClient(name = "user-service", configuration = CustomLoadBalanceRuleConfig.class)
@SpringBootApplication
public class Application {
			
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
