package com.bankOfHyrule.repositories;

import com.bankOfHyrule.models.Account;
import com.bankOfHyrule.models.AccountHistory;
import com.bankOfHyrule.models.BankManager;
import com.bankOfHyrule.util.CustomArrayList;
import com.bankOfHyrule.util.CustomException;

public interface BankManagerDAO {

	public BankManager login(BankManager m);
	public CustomArrayList<AccountHistory> viewHistory(int acc);
	public boolean closeAccount(int acc) throws CustomException;
	public boolean findAccount(Account account) throws CustomException;
	public double checkAmount(Account account) throws CustomException;
}
