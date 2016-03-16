package com.clubmycab.model;

public class UserModel {
	private String userName;
	private String phoneNumber;
	private String poolName;
	public boolean isOwner() {
		return isOwner;
	}
	public void setOwner(boolean isOwner) {
		this.isOwner = isOwner;
	}
	private boolean isOwner;

	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public void setPoolName(String poolName){
		this.poolName =  poolName;
	}
	
	public String getPoolName(){
		return poolName;
	}
	
	
	

}
