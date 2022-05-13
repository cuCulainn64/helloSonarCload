package com.bankOfHyrule.models;

import java.text.NumberFormat;
import java.time.LocalDate;

public class AccountHistory {

	private int tansactionId;
	private LocalDate date;
	private int memberAccount;
	private String amountFormated;
	
	public AccountHistory() {
		super();
	}


	public AccountHistory(LocalDate date, int memberAccount, double amount) {
		super();
		this.date = date;

		this.memberAccount = memberAccount;
		
		this.amountFormated = formatAmount(amount);
}


	public AccountHistory(int tansactionId, LocalDate date, int memberAccount, double amount) {
		super();
		this.tansactionId = tansactionId;
		this.date = date;

		this.memberAccount = memberAccount;
		
		this.amountFormated = formatAmount(amount);
		
	}
	
	/**
	 * Takes in a double and returns a string of that double amount formated in dollars
	 * */
	private String formatAmount(double amount) {
		// TODO Auto-generated method stub
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		return formatter.format(amount);
	}


	public int getTansactionId() {
		return tansactionId;
	}


	public void setTansactionId(int tansactionId) {
		this.tansactionId = tansactionId;
	}


	public LocalDate getDate() {
		return date;
	}


	public void setDate(LocalDate date) {
		this.date = date;
	}


	

	public int getMemberAccount() {
		return memberAccount;
	}


	public void setMemberAccount(int memberAccount) {
		this.memberAccount = memberAccount;
	}


	public String getAmount() {
		return amountFormated;
	}


	public void setAmount(double amount) {
		this.amountFormated = formatAmount(amount);
	}


	@Override
	public String toString() {
		return "AccountHistory [tansactionId=" + tansactionId + ", date=" + date + ", memberAccount=" + memberAccount + ", amount=" + amountFormated + "]";
	}	
	
}
