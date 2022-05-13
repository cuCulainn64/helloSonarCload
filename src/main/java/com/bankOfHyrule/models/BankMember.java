package com.bankOfHyrule.models;

public class BankMember {

	private String memberUsername;
	private String memberPassword;
	private String memberName;
	public BankMember() {
		super();
	}
	public BankMember(String memberUsername, String memberPassword) {
		super();
		this.memberPassword = memberPassword;
		this.memberUsername = memberUsername;
	}
	public BankMember(String memberUsername, String memberPassword, String memberName) {
		super();
		this.memberUsername = memberUsername;
		this.memberPassword = memberPassword;
		this.memberName = memberName;
	}
	public String getMemberUsername() {
		return memberUsername;
	}
	public void setMemberUsername(String memberUsername) {
		this.memberUsername = memberUsername;
	}
	public String getMemberPassword() {
		return memberPassword;
	}
	public void setMemberPassword(String memberPassword) {
		this.memberPassword = memberPassword;
	}
	public String getMemberName() {
		return memberName;
	}
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
	@Override
	public String toString() {
		return "BankMember [memberUsername=" + memberUsername + ", memberPassword=" + memberPassword + ", memberName="
				+ memberName + "]";
	}
	
}
