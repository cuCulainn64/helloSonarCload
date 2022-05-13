package com.bankOfHyrule.repositories;

import com.bankOfHyrule.models.Account;
import com.bankOfHyrule.models.AccountHistory;
import com.bankOfHyrule.models.BankMember;
import com.bankOfHyrule.util.CustomArrayList;
import com.bankOfHyrule.util.CustomException;

public interface BankMemberDAO {

	public BankMember login(BankMember m);
	
	public boolean deposit(int acc, double amount, String username);
	public boolean withdraw (int acc, double amount, String username);
	
	public Account viewBalance(int acc, String username) throws CustomException;
	public CustomArrayList<AccountHistory> viewHistory(String username);
	public CustomArrayList<AccountHistory> viewHistory(int acc, String username);
	public boolean transfer(int acc1, int acc2, String username, double amount);
	public int openNewAccount(String username, double amount);
	public boolean createNewMember(BankMember bankMember) throws CustomException;

	public CustomArrayList<AccountHistory> viewHistoryMax(int accountNumber, String username, double max);

	public CustomArrayList<AccountHistory> viewHistoryMin(int accountNumber, String username, double min);

	public CustomArrayList<AccountHistory> viewHistoryMinMax(int accountNumber, String username, double min,
			double max);


	public CustomArrayList<AccountHistory> viewOpening(int accountNumber, String username);

	CustomArrayList<AccountHistory> viewHistoryByDesc(int accountNumber, String username, boolean lookForDeposits);
	
	
}
