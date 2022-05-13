package com.bankOfHyrule.services;

import com.bankOfHyrule.models.AccountHistory;
import com.bankOfHyrule.util.CustomArrayList;

public interface BankMemberService {

	
	public boolean login(String username, String password);

	public CustomArrayList<AccountHistory> viewTransactionHistory(String username);

	public CustomArrayList<AccountHistory> viewTransactionHistory(int acc, String username);
	
	public String viewAmmount(int acc, String username);

	String withdraw(int acc, String username, double amount);

	String deposit(int acc11, String username, double amount1);

	String transfer(int acc2, int acc22, String username, double amount2);


	String openNewAccount(double ammount, String username);



	CustomArrayList<AccountHistory> viewTransactionHistoryByRange(int accountNumber, String username, double min,
			double max);

	CustomArrayList<AccountHistory> viewTransactionHistoryByType(int accountNumber, String username, int type);
	

	

}
