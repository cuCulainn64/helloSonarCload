package com.bankOfHyrule.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.log4j.Logger;

import com.bankOfHyrule.models.Account;
import com.bankOfHyrule.models.AccountHistory;
import com.bankOfHyrule.models.BankManager;
import com.bankOfHyrule.util.ConnectionUtil;
import com.bankOfHyrule.util.CustomArrayList;
import com.bankOfHyrule.util.CustomException;

public class BankManagerDAOImpl implements BankManagerDAO {
	
	private static Logger logger = Logger.getLogger(BankManagerDAOImpl.class);

/**
 * Checks to see if the manager's login credentials are valid.
 * If so, sets the manager name and returns the manager. If not, returns null
 * */
	@Override
	public BankManager login(BankManager m) {
		logger.info("In DAO layer: Manager login atempt...");
		
		
		try (Connection conn = ConnectionUtil.getConnection()) {
			String sql = "SELECT * FROM bank_managers WHERE username = ? AND manager_password = ?;";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, m.getManagerUsername());
			stmt.setString(2, m.getManagerPassword());
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()) {
				m.setManagerName(rs.getString(2));
			}else{
				return null;
			}

			logger.info("Manager login succesful");
			return m;
		} catch (SQLException e) {
			logger.warn("Unable to execute SQL statement", e);
			return null;
		}
	}

/**
 * returns the full account history of the passed account number
 * */
	
	@Override
	public CustomArrayList<AccountHistory> viewHistory(int acc) {
		
		logger.info("In DAO layer: Manager attempting to view account history...");
		CustomArrayList<AccountHistory> list = new CustomArrayList<AccountHistory>();

		try (Connection conn = ConnectionUtil.getConnection()) {
			
			
			
			String sql = "SELECT * FROM account_history WHERE member_account = ?;";
			PreparedStatement stmt = conn.prepareStatement(sql, 1,1);
			stmt.setInt(1, acc);
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
			if (rs != null) 
			{
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
	 * Deletes a row from accounts table where account number matches the given parameter
	 * Returns true if successful
	 * @throws CustomException 
	 * 
	 * */
	@Override
	public boolean closeAccount(int acc) throws CustomException {
logger.info("In DAO layer: manager attempting to close account");
		
		
		try (Connection conn = ConnectionUtil.getConnection()) {
			String sql = "Delete FROM accounts WHERE account_number = ? returning amount;";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, acc);
			
			
			
			if (stmt.execute()) {
				logger.info("account closed");
				return true;
			}else{
				
				logger.warn("could not close account");
				throw new CustomException("could not close account");
			}

		} catch (SQLException e) {
			logger.warn("Unable to execute SQL statement", e);
			throw new CustomException("Unable to execute SQL statement");
		}
	}
	


	/**
	 * Checks to see if the account that was passed in has a valid account number.
	 * @throws CustomException 
	 * */
	@Override
	public boolean findAccount(Account account) throws CustomException {
logger.info("In DAO layer. Finding account");
		
		
		try (Connection conn = ConnectionUtil.getConnection()) {
			String sql = "SELECT * FROM accounts WHERE account_number = ?;";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, account.getAccNumber());
			
			
			
			if (stmt.execute()) {
				logger.info("account closed");
				return true;
			}else{
				
				logger.warn("could not close account");
				throw new CustomException("could not close account");
			}

		} catch (SQLException e) {
			logger.warn("Unable to execute SQL statement", e);
			throw new CustomException("Unable to execute SQL statement");
		}
	}

	/**
	 * Checks the database to see how much is in the account passed to it
	 * @throws CustomException if unable to find the account or the amount in it
	 * */
	@Override
	public double checkAmount(Account account) throws CustomException {
logger.info("In DAO layer: manager checking the amount in an account");
		
		
		try (Connection conn = ConnectionUtil.getConnection()) {
			String sql = "SELECT amount FROM accounts WHERE account_number = ?;";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, account.getAccNumber());
			
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()) {
				logger.info("Found amount");
				return rs.getDouble(1);
			}else{
				
				logger.warn("could not find amount");
				throw new CustomException("could not find amount");
			}

		} catch (SQLException e) {
			logger.warn("Unable to execute SQL statement", e);
			throw new CustomException("could not find amount");
		}
	}

}
