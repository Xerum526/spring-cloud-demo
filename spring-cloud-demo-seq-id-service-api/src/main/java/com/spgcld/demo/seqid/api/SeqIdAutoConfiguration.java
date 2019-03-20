package com.spgcld.demo.seqid.api;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@EnableFeignClients("com.spgcld.demo.seqid.api.feign") //  启用内部代理
@EnableCircuitBreaker // 启用断路器
@ConditionalOnProperty(prefix = "seq-id-service.api.feign", name = {"enabled"}, havingValue = "true", matchIfMissing = false)
public class SeqIdAutoConfiguration {

}
