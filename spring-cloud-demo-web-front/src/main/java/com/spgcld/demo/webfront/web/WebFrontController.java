package com.spgcld.demo.webfront.web;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spgcld.demo.user.api.feign.UserFeignClient;
import com.spgcld.demo.webfront.utils.FastJsonConvertUtil;

@RestController
public class WebFrontController {

	@Autowired
	private UserFeignClient userFeignClient;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@RequestMapping(value = "/regist")
	public String regist(@RequestParam("userName") String userName, @RequestParam("code") String code, @RequestParam("loginPassword") String loginPassword) throws Exception {
		return userFeignClient.regist(userName, code, loginPassword);

	}

	@RequestMapping(value = "/login")
	@SuppressWarnings("unchecked")
	public String login(@RequestParam("userId") String userId, @RequestParam("loginPassword") String loginPassword)
			throws Exception {
		if (StringUtils.isBlank(loginPassword)) {
			return "login password is empty";
		}

		String userInfoJson = userFeignClient.queryUserInfo1(userId);

		if (StringUtils.isBlank(userInfoJson)) {
			return "user is not exist";
		}

		Map<String, String> map = FastJsonConvertUtil.convertJSONToObject(userInfoJson, Map.class);

		String _loginPassword = map.get("loginPassword");

		if (!loginPassword.equals(_loginPassword)) {
			return "login password is wrong";
		}
		HashOperations<String, Object, Object> cityHash = redisTemplate.opsForHash();
		String cacheTokenKey = "token-" + userId;
		String cacheTokenValue = String.valueOf(RandomUtils.nextInt(Integer.MAX_VALUE));
		Date currentTimeD = new Date();
		Date expireTimeD = DateUtils.addMinutes(currentTimeD, 30);
		String newCacheTokenExpireTime = DateFormatUtils.format(expireTimeD, "yyyyMMddHHmmss");
		String cacheToken = cacheTokenValue + "-" + newCacheTokenExpireTime;
		cityHash.put("user-token", cacheTokenKey, cacheToken);
		map.put("token", cacheTokenValue);
		return FastJsonConvertUtil.convertObjectToJSON(map);

	}

	@RequestMapping(value = "/queryUserInfo1")
	public String queryUserInfo1(@RequestParam("userId") String userId) throws Exception {
		return userFeignClient.queryUserInfo1(userId);

	}
	
	@RequestMapping(value = "/queryUserInfo2")
	public String queryUserInfo2(@RequestParam("userId") String userId) throws Exception {
		return userFeignClient.queryUserInfo2(userId);

	}

}
