package com.bankOfHyrule.models;

public class Account {
	private double amount;
	private int accNumber;
	private String username;
	@Override
	public String toString() {
		return "Account [amount=" + amount + ", accNumber=" + accNumber + ", username=" + username + "]";
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public int getAccNumber() {
		return accNumber;
	}
	public void setAccNumber(int accNumber) {
		this.accNumber = accNumber;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Account(double amount, int accNumber, String username) {
		super();
		this.amount = amount;
		this.accNumber = accNumber;
		this.username = username;
	}
	public Account(double amount, int accNumber) {
		super();
		this.amount = amount;
		this.accNumber = accNumber;
	}
	public Account() {
		super();
	}

}
