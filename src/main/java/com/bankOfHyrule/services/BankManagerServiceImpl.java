package com.bankOfHyrule.services;

import java.text.NumberFormat;

import org.apache.log4j.Logger;

import com.bankOfHyrule.models.Account;
import com.bankOfHyrule.models.AccountHistory;
import com.bankOfHyrule.models.BankManager;
import com.bankOfHyrule.models.BankMember;
import com.bankOfHyrule.repositories.BankManagerDAO;
import com.bankOfHyrule.repositories.BankManagerDAOImpl;
import com.bankOfHyrule.util.CustomArrayList;
import com.bankOfHyrule.util.CustomException;

public class BankManagerServiceImpl implements BankManagerService {

	private static Logger logger = Logger.getLogger(BankManagerServiceImpl.class);
	private BankManagerDAO managerDAO = new BankManagerDAOImpl();
	
	/**
	 * Returns a CustomArrayList<AccountHistory> containing the full account history of the given account number
	 * */
	@Override
	public CustomArrayList<AccountHistory> viewTransactionHistory(int acc) {

		logger.info("In service layer - manager attempting to view account history of account");
		BankManagerDAO manager = new BankManagerDAOImpl();
		CustomArrayList<AccountHistory> historyOBJ = manager.viewHistory(acc);
		if(historyOBJ.isEmpty()) {
			return null;
		}else {
			return historyOBJ;
		}
		
	}

	/**
	 * Takes in the account number to close and attempts to close that account.
	 * if successful, returns the amount of money in the account.
	 *  
	 * 
	 * */
	@Override
	public String closeAccount(int accountNumber) {
		
		Account account = new Account(0,accountNumber);
		BankManagerDAO manager = new BankManagerDAOImpl();
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		
		try {
			if(manager.findAccount(account)) {//throws exception if it can't find the account
				
				account.setAmount(manager.checkAmount(account));
				if(manager.closeAccount(account.getAccNumber())) {//throws exception if it can't close the account
					return "The account has been closed. The final balance was " + formatter.format(account.getAmount());
					
				}
			}
		} catch (CustomException e) {
			return e.getMessage();
		}
		return null;//can't be reached
		
	}


/**
 * Logs the login attempt and returns true if successful
 * */
	@Override
	public boolean login(String username, String password) {
		
		logger.info("In service layer - attempting manager login");
		BankManager m = new BankManager(username, password);
		if(managerDAO.login(m) == null) {
			return false;
		}
		System.out.println("Login success. Welcome " + m.getManagerName());
		return true;
	}

}
