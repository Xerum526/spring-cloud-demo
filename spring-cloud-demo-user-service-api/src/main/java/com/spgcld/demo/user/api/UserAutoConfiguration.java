package com.spgcld.demo.user.api;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@EnableFeignClients("com.spgcld.demo.user.api.feign") //  启用内部代理
@EnableCircuitBreaker // 启用断路器
@ConditionalOnProperty(prefix = "user-service.api.feign", name = {"enabled"}, havingValue = "true", matchIfMissing = false)
public class UserAutoConfiguration {

}
