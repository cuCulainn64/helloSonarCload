package com.bankOfHyrule.services;

import com.bankOfHyrule.models.AccountHistory;
import com.bankOfHyrule.util.CustomArrayList;
import com.bankOfHyrule.util.CustomException;

public interface BankManagerService {


	public CustomArrayList<AccountHistory> viewTransactionHistory(int acc);

	

	boolean login(String u, String p);
	String closeAccount(int accountNumber) throws CustomException;
}
