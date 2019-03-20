package com.spgcld.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.spgcld.demo.entity.User;
import com.spgcld.demo.mapper.UserMapper;
import com.spgcld.demo.seqid.api.feign.SeqIdFeignClient;

@Service
public class UserService {
	
	@Autowired
	private SeqIdFeignClient seqIdFeignClient;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	public User regist(String userName, String code, String loginPassword) throws Exception {
		User user = new User();
		String userId = seqIdFeignClient.getId("userId");
		user.setUserId(userId);
		user.setUserName(userName);
		user.setCode(code);
		user.setLoginPassword(loginPassword);
		int ret = userMapper.insertUserInfo(user);
		if(ret == 0) {
			return null;
		}
		redisTemplate.opsForHash().put("user-info", userId, user);
		return user;
	}

	public User queryUserInfo(String userId) {
		User user = (User) redisTemplate.opsForHash().get("user-info", userId);
		if (user != null) {
			return user;
		}
		user = userMapper.queryUserInfo(userId);
		return user;
	}

}
