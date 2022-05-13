package com.bankOfHyrule.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.log4j.Logger;

import com.bankOfHyrule.models.Account;
import com.bankOfHyrule.models.AccountHistory;
import com.bankOfHyrule.models.BankMember;
import com.bankOfHyrule.util.ConnectionUtil;
import com.bankOfHyrule.util.CustomArrayList;
import com.bankOfHyrule.util.CustomException;

public class BankMemberDAOImpl implements BankMemberDAO {
	private static Logger logger = Logger.getLogger(BankMemberDAOImpl.class);

	/**
	 * Checks to see if the member's login credentials are valid.
	 * If so, sets the member name and returns the member. If not, returns null
	 * */
	@Override
	public BankMember login(BankMember m) {
		logger.info("In DAO layer: Member login atempt...");

		try (Connection conn = ConnectionUtil.getConnection()) {
			String sql = "SELECT * FROM bank_members WHERE username = ? AND member_password = ?;";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, m.getMemberUsername());
			stmt.setString(2, m.getMemberPassword());
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				m.setMemberName(rs.getString(2));
			} else {
				return null;
			}

			logger.info("Member login succesful");
			return m;
		} catch (SQLException e) {
			logger.warn("Unable to execute SQL statement", e);
			return null;
		}
	}

	/**
	 * searches the database to see if the username is in the database
	 * */
	
	private boolean findUsername(String username){
		logger.info("In DAO layer: looking for a username...");

		try (Connection conn = ConnectionUtil.getConnection()) {
			String sql = "SELECT * FROM bank_members WHERE username = ?;";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			logger.warn("Unable to execute SQL statement", e);
			return false;
		}
	}
	
	
/**
 * searches the database for the entire history of the given user
 * */
	@Override
	public CustomArrayList<AccountHistory> viewHistory(String username) {
		logger.info("In DAO layer: member attempting to view all account history...");
		CustomArrayList<AccountHistory> list = new CustomArrayList<AccountHistory>();

		try (Connection conn = ConnectionUtil.getConnection()) {

			String sql = "SELECT account_history.amount, account_history.transaction_date, accounts.account_number FROM account_history INNER JOIN accounts ON accounts.account_number=account_history.member_account where accounts.member_username= ?;";
			PreparedStatement stmt = conn.prepareStatement(sql, 1, 1);
			stmt.setString(1, username);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {

				int account = rs.getInt("account_number");
				double price = rs.getDouble("amount");
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				String date = rs.getDate("transaction_date").toString();
				LocalDate locDate = LocalDate.parse(date, formatter);

				
				AccountHistory h = new AccountHistory(locDate, account, price);
				
				list.add(h);
			}

			int size = 0;
			if (rs != null) {
				rs.last(); 
				size = rs.getRow();
			}

			logger.info("List has been successfully retrieved. Number of transactions: " + size);
			
			rs.close();

			
			conn.close();
		} catch (SQLException e) {
			logger.warn("Unable to retrieve account history from the database", e);
		}

		return list;
	}

	/**
	 * searches the database for the account history of a specific account
	 * */
	@Override
	public CustomArrayList<AccountHistory> viewHistory(int acc, String username) {

		logger.info("In DAO layer: member attempting to view account history...");
		CustomArrayList<AccountHistory> list = new CustomArrayList<AccountHistory>();

		try (Connection conn = ConnectionUtil.getConnection()) {

			String sql = "SELECT account_history.amount, account_history.transaction_date, accounts.account_number FROM account_history INNER JOIN accounts ON accounts.account_number=account_history.member_account where accounts.member_username= ? AND accounts.account_number = ?;";
			PreparedStatement stmt = conn.prepareStatement(sql, 1, 1);
			stmt.setString(1, username);
			stmt.setInt(2, acc);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {

				int id = rs.getInt("transaction_id");
				int account = rs.getInt("member_account");
				double price = rs.getDouble("amount");
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				String date = rs.getDate("transaction_date").toString();
				LocalDate locDate = LocalDate.parse(date, formatter);

				AccountHistory h = new AccountHistory(id, locDate, account, price);
				
				list.add(h);
			}

			int size = 0;
			if (rs != null) {
				rs.last(); 
				size = rs.getRow(); 
			}

			logger.info("List has been successfully retrieved. Number of transactions: " + size);
			
			rs.close();
			conn.close();
		} catch (SQLException e) {
			logger.warn("Unable to retrieve account history from the database", e);
		}

		return list;
	}

	/**
	 * searches the database for an account using the provided account number and username.
	 * returns an account object containing the amount found, username, and account number
	 * */
	@Override
	public Account viewBalance(int acc, String username) throws CustomException {
		
		logger.info("In DAO layer: Member attempting to view balance...");

		try (Connection conn = ConnectionUtil.getConnection()) {
			String sql = "select amount from accounts where member_username = ? and account_number = ?;";
			PreparedStatement stmt = conn.prepareStatement(sql);

			stmt.setString(1, username);
			stmt.setInt(2, acc);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				logger.info("found balance");
				return new Account(rs.getDouble(1), acc, username);
			} else {
				logger.warn("Error, couldn't find balance");
				throw new CustomException("Error, couldn't find balance");
			}

		} catch (SQLException e) {
			logger.warn("Unable to execute SQL statement", e);
			throw new CustomException("Error, couldn't find balance");
		}
	}

	/**
	 * logs the attempt at deposit and returns if it was successful
	 * */
	@Override
	public boolean deposit(int acc, double amount, String username) {
		logger.info("In DAO layer: Member attempting to make a deposit");
		return updateBalance(acc, username, Math.abs(amount));//make sure deposit is positive
	}

	/**
	 * logs the attempt at withdrawal and returns if it was successful
	 * */
	@Override
	public boolean withdraw(int acc, double amount, String username) {
		logger.info("In DAO layer: Member attempting to make a withdrawal");
		return updateBalance(acc, username, -1 * Math.abs(amount));//make sure withdraw is negative
	}

	/**
	 * updates the account balance. Positive values passed in for amount will increase the balance.
	 * negative amounts will be subtracted.
	 * returns false if unsuccessful
	 * */
	private boolean updateBalance(int acc, String username, double amount) {

		logger.info("In DAO layer: updating balance");
		
		try {
			double newAmount = 0;
			Account account = viewBalance(acc, username);
			double oldAmount = account.getAmount();

			if (oldAmount + amount < 0) {// prevents overdraft
				return false;

			} else {
				newAmount = oldAmount +amount;
				try (Connection conn = ConnectionUtil.getConnection()) {
					String sql = "update acounts set amount = ? where member_username = ? and account_number = ? returning member_username";
					PreparedStatement stmt = conn.prepareStatement(sql);

					stmt.setDouble(1, newAmount);
					stmt.setString(2, username);
					stmt.setInt(3, acc);

					if (stmt.execute()) {
						logger.info("Account balance has been updated");

						return true;
					} else {
						logger.warn("Error, couldn't update balance");
						return false;
					}

				} catch (SQLException e) {
					logger.warn("Unable to execute SQL statement", e);

				}
			}
		} catch (CustomException e1) {
			logger.warn(e1);
			return false;
		}
		return false;

	}

	/**
	 * withdraws a set amount from acc1 and deposits it in acc2.
	 * returns false if unsuccessful
	 * */
	@Override
	public boolean transfer(int acc1, int acc2, String username, double amount) {

		logger.info("In DAO layer: attempting transfer");
		try {
			Account account1 = viewBalance(acc1, username);
			Account account2 = viewBalance(acc2, username);
			if (account1 != null && account2 != null) {
				logger.info("Transfering funds");
				return withdraw(acc1, amount, username) && deposit(acc2, amount, username);
			} else {
				return false;
			}
		} catch (CustomException e) {
			logger.info(e.getMessage());
			return false;
		}
	}
	
	/**
	 * attempt to open a new account
	 * returns the account number if successful
	 * returns -1 if unsuccessful
	 * */

	@Override
	public int openNewAccount(String username, double amount) {
		try {

			if (amount < 25) {
				return -1;

			} else {

				try (Connection conn = ConnectionUtil.getConnection()) {
					String sql = "insert into accounts (member_username, amount) values (?, ?) returnting account_number;";
					PreparedStatement stmt = conn.prepareStatement(sql);

					stmt.setString(1, username);
					stmt.setDouble(2, amount);

					ResultSet rs = stmt.executeQuery();
					if (rs.next()) {
						logger.info("New account openend.");

						return rs.getInt(1);
					} else {
						logger.warn("Error, couldn't find balance");
						throw new CustomException("Error, couldn't find new acount number");
					}

				} catch (SQLException e) {
					logger.warn("Unable to execute SQL statement", e);
				}
			}
		} catch (CustomException e) {
			// TODO Auto-generated catch block
			logger.warn(e.getMessage());
		}
		return -1;
	}

	/**
	 * checks to see if the passed member account is already in the database
	 * if not, adds the new bank member to the database and returns true
	 * */
	@Override
	public boolean createNewMember(BankMember bankMember) {
		logger.info("In DAO layer: Atempting to make a new member acount...");

		if (findUsername(bankMember.getMemberUsername())) {
			return false;
		} else {
			// create user

			try (Connection conn = ConnectionUtil.getConnection()) {

				//
				String sql = "insert into bank_members (username, member_name, member_password) values(?,?,?) returning member_name;";
				PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setString(1, bankMember.getMemberUsername());
				stmt.setString(2, bankMember.getMemberName());
				stmt.setString(3, bankMember.getMemberPassword());

				if (stmt.execute()) {
					logger.info("New member account created succesfully");
					return true;
				} else {
					logger.warn("failed to create new member account");
					return false;
				}

			} catch (SQLException e) {
				logger.warn("Unable to execute SQL statement", e);
				return false;
			}
		}

	}

	/**
	 * searches account history, limiting results to the results <= the max provided
	 * */
	@Override
	public CustomArrayList<AccountHistory> viewHistoryMax(int accountNumber, String username, double max) {
		logger.info("In DAO layer: member attempting to view account history... with a max transaction amount of " + max);
		CustomArrayList<AccountHistory> list = new CustomArrayList<AccountHistory>();

		try (Connection conn = ConnectionUtil.getConnection()) {

			String sql = "SELECT account_history.amount, account_history.transaction_date, accounts.account_number FROM account_history INNER JOIN accounts ON accounts.account_number=account_history.member_account where accounts.member_username= ? AND accounts.account_number = ? and account_history.amount <= ?;";
			PreparedStatement stmt = conn.prepareStatement(sql, 1, 1);
			stmt.setString(1, username);
			stmt.setInt(2, accountNumber);
			stmt.setDouble(3, max);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {

				int id = rs.getInt("transaction_id");
				int account = rs.getInt("member_account");
				double price = rs.getDouble("amount");
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				String date = rs.getDate("transaction_date").toString();
				LocalDate locDate = LocalDate.parse(date, formatter);

				
				AccountHistory h = new AccountHistory(id, locDate, account, price);
				
				list.add(h);
			}

			int size = 0;
			if (rs != null) {
				rs.last(); 
				size = rs.getRow(); 
			}

			logger.info("List has been successfully retrieved. Number of transactions: " + size);
			
			rs.close();

			
			conn.close();
		} catch (SQLException e) {
			logger.warn("Unable to retrieve account history from the database", e);
		}

		return list;
	}

	/**
	 * searches account history, limiting results to the results >= the min provided
	 * */
	@Override
	public CustomArrayList<AccountHistory> viewHistoryMin(int accountNumber, String username, double min) {
		logger.info("In DAO layer: member attempting to view account history... with a min transaction amount of " + min);
		CustomArrayList<AccountHistory> list = new CustomArrayList<AccountHistory>();

		try (Connection conn = ConnectionUtil.getConnection()) {

			String sql = "SELECT account_history.amount, account_history.transaction_date, accounts.account_number FROM account_history INNER JOIN accounts ON accounts.account_number=account_history.member_account where accounts.member_username= ? AND accounts.account_number = ? AND account_history.amount >= ?;";
			PreparedStatement stmt = conn.prepareStatement(sql, 1, 1);
			stmt.setString(1, username);
			stmt.setInt(2, accountNumber);
			stmt.setDouble(3, min);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {

				int id = rs.getInt("transaction_id");
				int account = rs.getInt("member_account");
				double price = rs.getDouble("amount");
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				String date = rs.getDate("transaction_date").toString();
				LocalDate locDate = LocalDate.parse(date, formatter);

				
				AccountHistory h = new AccountHistory(id, locDate, account, price);
				
				list.add(h);
			}

			int size = 0;
			if (rs != null) {
				rs.last();
				size = rs.getRow(); 
			}

			logger.info("List has been successfully retrieved. Number of transactions: " + size);
			
			rs.close();

			conn.close();
		} catch (SQLException e) {
			logger.warn("Unable to retrieve account history from the database", e);
		}

		return list;
	}

	/**
	 * searches account history, limiting results to the results in the range of [min, max]
	 * */
	@Override
	public CustomArrayList<AccountHistory> viewHistoryMinMax(int accountNumber, String username, double min,
			double max) {
		logger.info("In DAO layer: member attempting to view account history... min " + min + " max " + max);
		CustomArrayList<AccountHistory> list = new CustomArrayList<AccountHistory>();

		try (Connection conn = ConnectionUtil.getConnection()) {

			String sql = "SELECT account_history.amount, account_history.transaction_date, accounts.account_number FROM account_history INNER JOIN accounts ON accounts.account_number=account_history.member_account where accounts.member_username= ? AND accounts.account_number = ? AND account_history.amount <= ? AND account_history.amount >= ?;";
			PreparedStatement stmt = conn.prepareStatement(sql, 1, 1);
			stmt.setString(1, username);
			stmt.setInt(2, accountNumber);
			stmt.setDouble(3, max);
			stmt.setDouble(4, min);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {

				int id = rs.getInt("transaction_id");
				int account = rs.getInt("member_account");
				double price = rs.getDouble("amount");
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				String date = rs.getDate("transaction_date").toString();
				LocalDate locDate = LocalDate.parse(date, formatter);

				AccountHistory h = new AccountHistory(id, locDate, account, price);
			
				list.add(h);
			}

			int size = 0;
			if (rs != null) {
				rs.last();
				size = rs.getRow(); 
			}

			logger.info("List has been successfully retrieved. Number of transactions: " + size);
			
			rs.close();

			
			conn.close();
		} catch (SQLException e) {
			logger.warn("Unable to retrieve account history from the database", e);
		}

		return list;
	}

	/**
	 * searches account history, looking for deposits if looksForDeposits is true.
	 * If not, searches for withdraws
	 * */
	@Override
	public CustomArrayList<AccountHistory> viewHistoryByDesc(int accountNumber, String username, boolean lookForDeposits) {
		logger.info("In DAO layer: member attempting to view account history by description...");
		CustomArrayList<AccountHistory> list = new CustomArrayList<AccountHistory>();

		try (Connection conn = ConnectionUtil.getConnection()) {

			String sql;
			if(lookForDeposits) {
				sql = "SELECT account_history.amount, account_history.transaction_date, accounts.account_number FROM account_history INNER JOIN accounts ON accounts.account_number=account_history.member_account where accounts.member_username= ? AND accounts.account_number = ? and amount >= 0;";
			}else {
				sql = "SELECT account_history.amount, account_history.transaction_date, accounts.account_number FROM account_history INNER JOIN accounts ON accounts.account_number=account_history.member_account where accounts.member_username= ? AND accounts.account_number = ? and amount < 0;";
			}
			
			
			PreparedStatement stmt = conn.prepareStatement(sql, 1, 1);
			stmt.setString(1, username);
			stmt.setInt(2, accountNumber);
			

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {

				int id = rs.getInt("transaction_id");
				int account = rs.getInt("member_account");
				double price = rs.getDouble("amount");
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				String date = rs.getDate("transaction_date").toString();
				LocalDate locDate = LocalDate.parse(date, formatter);

				
				AccountHistory h = new AccountHistory(id, locDate, account, price);
				
				list.add(h);
			}

			int size = 0;
			if (rs != null) {
				rs.last();
				size = rs.getRow(); 
			}

			logger.info("List has been successfully retrieved. Number of transactions: " + size);
			
			rs.close();

			
			conn.close();
		} catch (SQLException e) {
			logger.warn("Unable to retrieve account history from the database", e);
		}

		return list;
	}

	
	/**
	 * searches database for the account history entry with the earliest transaction date
	 * */

	@Override
	public CustomArrayList<AccountHistory> viewOpening(int accountNumber, String username) {
		
		logger.info("In DAO layer: member attempting to view account opening...");
		CustomArrayList<AccountHistory> list = new CustomArrayList<AccountHistory>();

		
		//SELECT account_history.amount, account_history.transaction_date, accounts.account_number FROM account_history INNER JOIN accounts ON accounts.account_number=account_history.member_account where accounts.member_username= ? AND accounts.account_number = ? order by account_history.transaction_date asc limit 1;
		try (Connection conn = ConnectionUtil.getConnection()) {

			String sql = "SELECT account_history.amount, account_history.transaction_date, accounts.account_number FROM account_history INNER JOIN accounts ON accounts.account_number=account_history.member_account where accounts.member_username= ? AND accounts.account_number = ? order by account_history.transaction_date asc limit 1;";
			PreparedStatement stmt = conn.prepareStatement(sql, 1, 1);
			stmt.setString(1, username);
			stmt.setInt(2, accountNumber);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {

				int id = rs.getInt("transaction_id");
				int account = rs.getInt("member_account");
				double price = rs.getDouble("amount");
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				String date = rs.getDate("transaction_date").toString();
				LocalDate locDate = LocalDate.parse(date, formatter);

				// 2. make a object that matches that record info
				AccountHistory h = new AccountHistory(id, locDate, account, price);
				// 3. add item into our list
				list.add(h);
			}

			int size = 0;
			if (rs != null) {
				rs.last(); // moves cursor to the last row
				size = rs.getRow(); // get row id
			}

			logger.info("List has been successfully retrieved. Number of transactions: " + size);
			// 4. close the resultSet
			rs.close();

			// 5. close connection
			conn.close();
		} catch (SQLException e) {
			logger.warn("Unable to retrieve account history from the database", e);
		}

		return list;
	}

}
