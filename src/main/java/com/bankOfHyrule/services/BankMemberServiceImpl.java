package com.bankOfHyrule.services;

import java.text.NumberFormat;

import org.apache.log4j.Logger;

import com.bankOfHyrule.models.AccountHistory;
import com.bankOfHyrule.models.BankMember;
import com.bankOfHyrule.repositories.BankMemberDAO;
import com.bankOfHyrule.repositories.BankMemberDAOImpl;
import com.bankOfHyrule.util.CustomArrayList;
import com.bankOfHyrule.util.CustomException;

public class BankMemberServiceImpl implements BankMemberService {
	private static Logger logger = Logger.getLogger(BankMemberServiceImpl.class);

	private BankMemberDAO memberDAO = new BankMemberDAOImpl();

	/**
	 * Logs the login attempt and returns true if successful
	 * */
	@Override
	public boolean login(String username, String password) {
		logger.info("In service layer - attempting manager login");
		BankMember m = new BankMember(username, password);
		if (memberDAO.login(m) == null) {
			return false;
		}
		System.out.println("Login success. Welcome " + m.getMemberName());
		return true;
	}
/**
 * Logs the attempt to view the account and returns the null if no history is found 
 * */
	@Override
	public CustomArrayList<AccountHistory> viewTransactionHistory(int acc, String username) {
		logger.info("In service layer - attempting to view account history of account");
		BankMemberDAO member = new BankMemberDAOImpl();
		CustomArrayList<AccountHistory> historyOBJ = member.viewHistory(acc, username);
		if (historyOBJ.isEmpty()) {
			return null;
		} else {
			return historyOBJ;
		}
	}

	/**
	 * Logs the attempt to view full account history and returns null if no history is found
	 * */
	@Override
	public CustomArrayList<AccountHistory> viewTransactionHistory(String username) {
		logger.info("In service layer - attempting to view account history of all accounts ");
		BankMemberDAO member = new BankMemberDAOImpl();
		CustomArrayList<AccountHistory> historyOBJ = member.viewHistory(username);
		if (historyOBJ.isEmpty()) {
			return null;
		} else {
			return historyOBJ;
		}
	}

	/**
	 * Logs the attempt to view the amount in an account and returns the amount formated in dollars
	 * */
	@Override
	public String viewAmmount(int acc, String username) {
		logger.info("In service layer - attempting to view current balance of account ");
		BankMemberDAO member = new BankMemberDAOImpl();
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		String result = null;
		try {
			result = formatter.format(member.viewBalance(acc, username).getAmount());
		} catch (CustomException e) {
			// TODO Auto-generated catch block
			result = e.getMessage();
			logger.error(e.getMessage());
		}
		return result;
	}

	/**
	 * Logs the withdrawal attempt and returns an appropriate message
	 * */
	@Override
	public String withdraw(int acc, String username, double amount) {
		logger.info("In service layer - member attempting to withdraw");
		BankMemberDAO member = new BankMemberDAOImpl();

		String result = null;
		if (member.withdraw(acc, amount, username)) {
			logger.info("withdrawal success");
			result = "Success";
		} else {
			logger.warn("Withdraw failed");
			result = "Something went wrong";
		}
		return result;
	}

	/**
	 * Logs the deposit attempt and returns an appropriate message
	 * */
	@Override
	public String deposit(int acc, String username, double amount) {
		
		logger.info("In service layer - member attempting to deposit");
		BankMemberDAO member = new BankMemberDAOImpl();

		String result = null;
		
			if (member.deposit(acc, amount, username)) {
				logger.info("deposit success");
				result = "Success";
			} else {
				logger.warn("deposit failed");
				result = "Something went wrong";
			}
		
		return result;
	}

	/**
	 * Logs the transfer attempt and returns an appropriate message
	 * */
	@Override
	public String transfer(int acc1, int acc2, String username, double amount) {
		
		logger.info("In service layer - member attempting to transfer funds");
		BankMemberDAO member = new BankMemberDAOImpl();

		String result = null;
		
			if (member.transfer(acc1, acc2, username, amount)) {
				logger.info("transfer success");
				result = "Success";
			}else {
				logger.warn("transfer failed");
				result = "Something went wrong";
			}
		
		return result;
	}

	/**
	 * Logs the attempt to open a new account
	 * if the initial deposit amount is less than 25, the new account is rejected
	 * returns an appropriate message
	 * */	
	@Override
	public String openNewAccount(double amount, String username) {
		logger.info("In service layer - member attempting to open an account");
		BankMemberDAO member = new BankMemberDAOImpl();
		int newAccount = 0;
		if (amount < 25) {
			logger.warn("initial deposit too small. New account rejected");
			return "Error, minimum to open an account is $25.00";
		} else {
			logger.info("initial deposit large enough. New account accepted");
			newAccount = member.openNewAccount(username, amount);
		}
		logger.info("checking for error code");
		if (newAccount < 0) {
			logger.warn("Error, failed to open account");
			return "Error, failed to open account";
		} else {
			logger.info("account opened successfully");
			return "New account opened. Account number: " + newAccount;
		}

	}
	
	/**
	 * checks the set range and calls the appropriate view transaction history method
	 * */

	@Override
	public CustomArrayList<AccountHistory> viewTransactionHistoryByRange(int accountNumber, String username, double min,
			double max) {
		logger.info("In service layer - attempting to view account history of account " + accountNumber
				+ " filtering between " + min + " - " + max);
		BankMemberDAO member = new BankMemberDAOImpl();
		CustomArrayList<AccountHistory> historyOBJ;
		if (min == -1) {
			if (max == -1) {
				// no min or max
				logger.info("no min or max set, searching for all history");
				historyOBJ = member.viewHistory(accountNumber, username);
			} else {
				// no min, max set
				logger.info("no min set, searching for all history up to max transaction size");
				historyOBJ = member.viewHistoryMax(accountNumber, username, max);
			}
		} else {
			if (max == -1) {
				// min set, no max
				logger.info("no max set, searching for all history above minimum transaction amount");
				historyOBJ = member.viewHistoryMin(accountNumber, username, min);
			} else {
				// min and max set
				logger.info("searching for all history in set range");
				historyOBJ = member.viewHistoryMinMax(accountNumber, username, min, max);
			}
		}

		if (historyOBJ.isEmpty()) {
			return null;
		} else {
			return historyOBJ;
		}
	}

	/**
	 * checks the value of type to determine what transaction history to search for.
	 * type = 1, searches for the opening of the account
	 * type = 2, searches for deposits
	 * type = 3 searches for withdrawals
	 * all other values returns the full history
	 * */
	@Override
	public CustomArrayList<AccountHistory> viewTransactionHistoryByType(int accountNumber, String username, int type) {
		logger.info("In service layer - attempting to view account history of account filtered by type of transaction");
		BankMemberDAO member = new BankMemberDAOImpl();
		CustomArrayList<AccountHistory> historyOBJ = null;
		switch (type) {
		case 1:
			logger.info("searching for account opening");
			historyOBJ = member.viewOpening(accountNumber, username);
			break;
		case 2:
			logger.info("searching for deposits");
			historyOBJ = member.viewHistoryByDesc(accountNumber, username, true);
			break;
		case 3:
			logger.info("searching for withdrawals");
			historyOBJ = member.viewHistoryByDesc(accountNumber, username, false);
			break;
		default:
			logger.info("no filter set, searching for full history");
			historyOBJ = member.viewHistory(accountNumber, username);
		}

		return historyOBJ;
	}

	

}
