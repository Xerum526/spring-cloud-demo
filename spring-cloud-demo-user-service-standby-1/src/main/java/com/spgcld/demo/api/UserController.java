package com.spgcld.demo.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.spgcld.demo.entity.User;
import com.spgcld.demo.service.UserService;
import com.spgcld.demo.utils.FastJsonConvertUtil;

@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/regist")
	public String regist(@RequestParam("userName") String userName, @RequestParam("code") String code, @RequestParam("loginPassword") String loginPassword) throws Exception {
		User user = userService.regist(userName, code, loginPassword);
		if (user == null) {
			return "regist fail";
		}
		return FastJsonConvertUtil.convertObjectToJSON(user);
	}

	@HystrixCommand(commandKey = "queryUserInfo1", commandProperties = {
			@HystrixProperty(name = "execution.timeout.enabled", value = "true"),
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000") }, fallbackMethod = "queryUserInfo1FallBack"

	)
	@RequestMapping(value = "/queryUserInfo1")
	public String queryUserInfo1(@RequestParam("userId") String userId) throws Exception {
		System.out.println("user-service-standby-1, queryUserInfo1, userId: " + userId);
		User user = userService.queryUserInfo(userId);
		return FastJsonConvertUtil.convertObjectToJSON(user);
	}
	
	public String queryUserInfo1FallBack(@RequestParam("userId") String userId) {
		return "queryUserInfo1FallBack, userId: " + userId;
	}
	
	@HystrixCommand(commandKey = "queryUserInfo2", commandProperties = {
			@HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE"),
			// 并发超过3个请求进行降级
			@HystrixProperty(name = "execution.isolation.semaphore.maxConcurrentRequests", value = "3") }, fallbackMethod = "queryUserInfo2FallBack"

	)
	@RequestMapping(value = "/queryUserInfo2")
	public String queryUserInfo2(@RequestParam("userId") String userId) throws Exception {
		System.out.println("user-service-standby-1, queryUserInfo2, userId: " + userId);
		User user = userService.queryUserInfo(userId);
		return FastJsonConvertUtil.convertObjectToJSON(user);
	}
	
	public String queryUserInfo2FallBack(@RequestParam("userId") String userId) {
		return "queryUserInfo2FallBack, userId: " + userId;
	}

}
