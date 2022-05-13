package com.bankOfHyrule.models;

public class BankManager {

	private String managerUsername;
	private String managerPassword;
	private String managerName;
	
	public BankManager() {
		super();
	}

	public BankManager(String managerUsername, String managerPassword) {
		super();
		this.managerPassword = managerPassword;
		this.managerUsername = managerUsername;
	}

	public BankManager(String managerUsername, String managerPassword, String managerName) {
		super();
		this.managerUsername = managerUsername;
		this.managerPassword = managerPassword;
		this.managerName = managerName;
	}

	public String getManagerUsername() {
		return managerUsername;
	}

	public void setManagerUsername(String managerUsername) {
		this.managerUsername = managerUsername;
	}

	public String getManagerPassword() {
		return managerPassword;
	}

	public void setManagerPassword(String managerPassword) {
		this.managerPassword = managerPassword;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	@Override
	public String toString() {
		return "BankManager [managerUsername=" + managerUsername + ", managerPassword=" + managerPassword
				+ ", managerName=" + managerName + "]";
	}
	
}
