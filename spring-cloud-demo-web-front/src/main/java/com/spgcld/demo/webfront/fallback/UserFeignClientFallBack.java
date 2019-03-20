package com.spgcld.demo.webfront.fallback;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.spgcld.demo.user.api.feign.UserFeignClient.UserFeignClientHystrixFallBack;
import com.spgcld.demo.webfront.utils.FastJsonConvertUtil;

@Component
public class UserFeignClientFallBack extends UserFeignClientHystrixFallBack{
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Override
	public String regist(String userName, String code, String loginPassword) throws Exception {
      
		return "user regist fallback";
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public String queryUserInfo1(String userId) throws Exception {
		System.out.println("查询用户信息降级方法1,userId: " + userId);
		Map<String, String> map = (Map<String, String>) redisTemplate.opsForHash().get("user-info", userId);		
		return FastJsonConvertUtil.convertObjectToJSON(map);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String queryUserInfo2(String userId) throws Exception {
		System.out.println("查询用户信息降级方法2,userId: " + userId);
		Map<String, String> map = (Map<String, String>) redisTemplate.opsForHash().get("user-info", userId);		
		return FastJsonConvertUtil.convertObjectToJSON(map);
	}

}
