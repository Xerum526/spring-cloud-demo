package com.spgcld.demo.user.api.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = UserFeignConstants.FEIGN_NAME, fallback = UserFeignClient.UserFeignClientHystrixFallBack.class)
public interface UserFeignClient {
	
	@RequestMapping(value = "/regist")
	public String regist(@RequestParam("userName") String userName, @RequestParam("code") String code, @RequestParam("loginPassword") String loginPassword) throws Exception;
	
	@RequestMapping(value = "/queryUserInfo1")
	public String queryUserInfo1(@RequestParam("userId") String userId) throws Exception;
	
	@RequestMapping(value = "/queryUserInfo2")
	public String queryUserInfo2(@RequestParam("userId") String userId) throws Exception;
		
	public abstract class UserFeignClientHystrixFallBack implements UserFeignClient{
		
	}

}
