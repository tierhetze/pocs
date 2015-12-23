package com.beep02.ps.user;


/**
Example object, that consists of user attributes

@author Vladimir Nabokov

 **/

public class User {
	
	private String username;
	private Long loginTime;
	private String ip;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Long getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(Long loginTime) {
		this.loginTime = loginTime;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}

	
	
}
