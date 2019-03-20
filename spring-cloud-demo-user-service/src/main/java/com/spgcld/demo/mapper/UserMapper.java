package com.spgcld.demo.mapper;

import com.spgcld.demo.entity.User;

import tk.mybatis.mapper.common.BaseMapper;

public interface UserMapper extends BaseMapper<User> {
	
	int insertUserInfo(User user);
	
	User queryUserInfo(String userId);

}
