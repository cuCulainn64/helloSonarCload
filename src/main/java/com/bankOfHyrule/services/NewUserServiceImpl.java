package com.bankOfHyrule.services;


import org.apache.log4j.Logger;

import com.bankOfHyrule.models.BankMember;
import com.bankOfHyrule.repositories.BankMemberDAO;
import com.bankOfHyrule.repositories.BankMemberDAOImpl;
import com.bankOfHyrule.util.CustomException;

public class NewUserServiceImpl implements NewUserService {
	private static Logger logger = Logger.getLogger(NewUserServiceImpl.class);
	/**
	 * Logs attempt to create new user. If with the username or password has a space in it, the new user is rejected
	 * returns an appropriate message
	 * */
	public String createUser(String username, String password, String name) {
		logger.info("In service layer - attempting to view account history of account filtered by type of transaction");
		BankMemberDAO memberDAO = new BankMemberDAOImpl();
		String result = null;
		if(!username.matches("/^\\S*$/")&& !password.matches("/^\\S*$/")) {
			logger.info("checking if username is available");
			try {
				if( memberDAO.createNewMember(new BankMember(username, password, name))) {
					logger.info("username was availabe");
					return "Success";
				}else {
					logger.info("usernam was unavailable");
					return "Error, username unavailable";
				}
			} catch (CustomException e) {
				logger.info(e.getMessage());
				result = e.getMessage();
				
			}
		}else {
			logger.warn("username and or password had a space in it");
			return "No spaces allowed";
		}
		return result;
	}

}
