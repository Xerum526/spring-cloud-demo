package com.spgcld.demo.zuul.filter;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

@Component
public class AuthZuulFilter extends ZuulFilter {

	@Value("${zuul.authFilter.ignoreUrl}")
	private String[] ignoreUrl;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();
			
		// 判断是否需要验证
		boolean needCheck = needCheck(request);		
		if(!needCheck) {
			return ctx;
		}
		
		// 获取请求携带的token
		String token = request.getHeader("token");
		if(StringUtils.isBlank(token)) {
			setResponseErrorInfo(ctx, 401, "token is empty!");
			return null;
		}
		HashOperations<String, Object, Object> cityHash = redisTemplate.opsForHash();
		String userId = request.getParameter("userId");
		String cacheTokenKey = "token-" + userId;
		String cacheToken = (String) cityHash.get("user-token", cacheTokenKey);
		if(StringUtils.isBlank(cacheToken)) {
			setResponseErrorInfo(ctx, 401, "token is not exist");
			return null;
		}
		String[] cacheTokenValueAry = cacheToken.split("-");
		String cacheTokenValue = cacheTokenValueAry[0];
		if(!token.equals(cacheTokenValue)) {
			setResponseErrorInfo(ctx, 401, "token is error!");
			return null;
		}
		String cacheTokenExpireTime = cacheTokenValueAry[1];
		Date currentTimeD = new Date();
		String currentTimeS = DateFormatUtils.format(currentTimeD, "yyyyMMddHHmmss");
		if (Long.parseLong(currentTimeS) >= Long.parseLong(cacheTokenExpireTime)) {
			cityHash.delete("user-token", cacheTokenKey);
		} else {
			// 重新设置缓存里的token和过期时间
			Date expireTimeD = DateUtils.addMinutes(currentTimeD, 30);
			String newCacheTokenExpireTime = DateFormatUtils.format(expireTimeD, "yyyyMMddHHmmss");
			cacheToken = cacheTokenValue + "-" + newCacheTokenExpireTime;
			cityHash.put("user-token", cacheTokenKey, cacheToken);
			// 如果验证OK,设置请求信息
			ctx.addZuulRequestHeader("token", token);
		}
		return ctx;
	}

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return 0;
	}

	private boolean needCheck(HttpServletRequest request) {
		boolean needCheck = true;
		String uri = request.getRequestURI();
		for (String igUri : ignoreUrl) {
			if (uri.startsWith(igUri)) {
				needCheck = false;
				break;
			}
		}
		return needCheck;
	}

	private void setResponseErrorInfo(RequestContext ctx, int responseStatusCode, String responseBody) {
		ctx.setSendZuulResponse(false);
		ctx.setResponseStatusCode(responseStatusCode);
		ctx.setResponseBody(responseBody);
	}

}
