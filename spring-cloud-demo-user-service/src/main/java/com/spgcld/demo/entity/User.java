package com.spgcld.demo.entity;

import java.io.Serializable;

public class User implements Serializable {

	private static final long serialVersionUID = -7578735163475880948L;

	private String userId;

	private String userName;

	private String code;

	private String loginPassword;


	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}
	
}
