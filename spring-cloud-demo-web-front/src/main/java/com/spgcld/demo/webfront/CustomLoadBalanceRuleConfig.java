package com.spgcld.demo.webfront;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.loadbalancer.IRule;
import com.spgcld.demo.webfront.loadbalance.RoundStepLoadBalanceRule;

@Configuration
public class CustomLoadBalanceRuleConfig {
	
	@Bean
	public IRule roundStepRule() {
		return new RoundStepLoadBalanceRule();
	}

}
