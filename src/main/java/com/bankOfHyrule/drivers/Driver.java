package com.bankOfHyrule.drivers;

import java.util.InputMismatchException;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.bankOfHyrule.models.AccountHistory;
import com.bankOfHyrule.services.BankManagerServiceImpl;
import com.bankOfHyrule.services.BankMemberServiceImpl;
import com.bankOfHyrule.services.NewUserServiceImpl;
import com.bankOfHyrule.util.CustomArrayList;
import com.bankOfHyrule.util.CustomException;

public class Driver {
	private static Logger logger = Logger.getLogger(Driver.class);

	// use buffered readers so it can read spaces

	/*
	 * As any kind of user, I can: - [X] login with my existing credentials
	 * 
	 * As a bank member, I can: - [x] register myself for a new user account with
	 * the system (must be secured with a password) - [x] create at least one
	 * account (minimun opening account must be $25) - [x] deposit funds into an
	 * account (use doubles, not ints) - [x] withdraw funds from an account (no
	 * overdrafting!) - [x] view the balance of my account(s) (all balance displays
	 * must be in proper currency format) - [x] view all transaction history for an
	 * account - [x] filter the transaction history view for an account by a range
	 * of currency or by type of transaction - [x] transfer money between accounts
	 * 
	 * As a bank manager, I can: - [x] view all transaction history for all accounts
	 * - [x] can close an account
	 */
	public static void main(String[] args) {

		mainMenu();
	}

	private static void mainMenu() {
		System.out.println("Welcome to the Bank of Hyrule console application!");

		boolean quit = false;

		Scanner in = new Scanner(System.in);
		System.out.println("Hello and welcome to the Bank of Hyrule\n");

		do {
			// ask user what they want to do
			// switch based on user input

			System.out.println("Please select what you would like from the following:");
			System.out.println("1| Create new account");
			System.out.println("2| Bank member login");
			System.out.println("3| Bank manager login");
			System.out.println("0| End program");

			int choice = -1;

			if (in.hasNextInt()) {
				choice = in.nextInt();
			} else {
				in.next();
			}

			switch (choice) {
			case 1:
				createNewUser(in);
				break;
			case 2:
				memberLogin(in);
				// member login
				break;
			case 3:
				managerLogin(in);
				// manager login
				break;
			case 0:
				quit = true;
				break;
			default:
				System.out.println("Error: invalid selection");
			}

		} while (quit != true);

		// TODO close everything and end the program
		in.close();
		System.out.println("Goodbye");
	}

	private static void memberLogin(Scanner in) {
		BankMemberServiceImpl member = new BankMemberServiceImpl();

		System.out.println("Pleanse enter username and password");
		System.out.print("Username: ");
		String username = in.next();

		System.out.print("Password: ");
		String password = in.next();// replace with calls to system.console

		if (member.login(username, password)) {
			password = null;
			boolean quit = false;
			do {
				System.out.println("Please select what you would like from the following:");
				System.out.println("1| check account history for specific account");
				System.out.println("2| check full account history");
				System.out.println("3| View the current balance of an account");
				System.out.println("4| Withdraw from an account");
				System.out.println("5| Deposit into an account");
				System.out.println("6| Transfer funds from one account to another");
				System.out.println("7| Open a new account");
				System.out.println("0| go back");

				int choice = -1;

				if (in.hasNextInt()) {
					choice = in.nextInt();
				} else {
					in.next();
				}

				switch (choice) {
				case 1:
					int accountNumber = 0;
					int filterChoice = 0;
					try {
						System.out.print("Please enter the account number: ");
						accountNumber = in.nextInt();
						System.out.println("Would you like to filter your results?");
						System.out.println("1| No filters");
						System.out.println("2| Filter by range of currency");
						System.out.println("3| filter by type of transaction");
						filterChoice = in.nextInt();
					} catch (InputMismatchException e1) {
						logger.warn("Invalid input");
						System.out.println("Invalid input");
					}
					CustomArrayList<AccountHistory> list = null;
					switch (filterChoice) {
					case 1:
						list = member.viewTransactionHistory(accountNumber, username);
						break;
					case 2:
						try {
							System.out.println("Please enter the minimum currency. -1 for no minimum");
							double min = in.nextDouble();
							System.out.println("Please enter the maximum currency. -1 for no maximum");
							double max = in.nextDouble();
							list = member.viewTransactionHistoryByRange(accountNumber, username, min, max);
						} catch (InputMismatchException e) {
							logger.warn("Input must be double");
							System.out.println("Invalid input. Numbers and decimal points only");
						}
						break;
					case 3:
						System.out.println(
								"Please enter the type of transaction to search for. Note: transfers are conducted via a withdraw and a deposit");
						System.out.println("1| open");
						System.out.println("2| deposit");
						System.out.println("3| withdraw");
						try {
							int type = in.nextInt();
							list = member.viewTransactionHistoryByType(accountNumber, username, type);
						} catch (InputMismatchException e) {
							logger.warn("Input must be integer");
							System.out.println("Invalid input. Inout needs to be an integer");
						}
						break;
					default:
						list = null;

					}

					if (!(list == null)) {
						if (list.isEmpty()) {
							System.out.println("Error could not find account");
						} else {

							for (int i = 0; i < list.size(); i++) {
								System.out.println(list.get(i).toString());
							}
						}
					} else {
						System.out.println("Error, could not find account");
					}

					break;
				case 2:

					CustomArrayList<AccountHistory> list1 = member.viewTransactionHistory(username);

					if (!(list1 == null)) {
						if (list1.isEmpty()) {
							System.out.println("Error could not find account");
						} else {
							for (int i = 0; i < list1.size(); i++) {
								System.out.println(list1.get(i).toString());
							}
						}
					} else {
						System.out.println("Error could not find account");
					}

				case 3:
					System.out.println("Please enter the account number");
					try {
						int acc = in.nextInt();
						System.out.println(member.viewAmmount(acc, username));
					} catch (InputMismatchException e1) {
						logger.warn("Invalid input");
						System.out.println("Invalid input");
					}
					break;

				case 4:
					try {
						System.out.println("Please enter the account number");
						int acc1 = in.nextInt();
						System.out.println(member.viewAmmount(acc1, username));
						System.out.println("Please enter the amount to withdraw");

						double amount = in.nextDouble();// change to string and parse the money
						System.out.println(member.withdraw(acc1, username, amount));
					} catch (InputMismatchException e) {
						logger.warn("Invalid input");
						System.out.println("Invalid input");
					}
					break;
				case 5:
					try {
						System.out.println("Please enter the account number");
						int acc11 = in.nextInt();
						System.out.println(member.viewAmmount(acc11, username));
						System.out.println("Please enter the amount to deposit");
						double amount1 = in.nextDouble();// change to string and parse the money
						System.out.println(member.deposit(acc11, username, amount1));
					} catch (InputMismatchException e) {
						logger.warn("Invalid input");
						System.out.println("Invalid input");
					}
					break;
				case 6:
					try {
						System.out.println("Please enter the account to transfer money from");
						int acc2 = in.nextInt();
						System.out.println("Please enter the account to transfer money to");
						int acc3 = in.nextInt();
						System.out.println("Please enter the amount of money to transfer");

						double amount2 = in.nextDouble();// change to string and parse amount
						System.out.println(member.transfer(acc2, acc3, username, amount2));
					} catch (InputMismatchException e) {
						logger.warn("Invalid input");
						System.out.println("Invalid input");
					}
					break;
				case 7:
					try {
						System.out.println("Please enter the amount to open the account with. Minimum $25.00");
						double response = in.nextDouble();
						System.out.println(member.openNewAccount(response, username));
					} catch (InputMismatchException e) {
						logger.warn("Invalid input");
						System.out.println("Invalid input");
					}

					break;
				case 0:
					quit = true;
					break;
				default:
					System.out.println("Error: invalid selection");
				}

			} while (quit != true);
		} else {
			System.out.println("wrong username/password");
		}

	}

	private static void managerLogin(Scanner in) {

		BankManagerServiceImpl manager = new BankManagerServiceImpl();

		System.out.println("Pleanse enter username and password");
		System.out.print("Username: ");
		String username = in.next();

		System.out.print("Password: ");
		String password = in.next();// replace with calls to system.console

		if (manager.login(username, password)) {
			// success
			password = null;
			boolean quit = false;
			do {
				// ask user what they want to do
				// switch based on user input

				System.out.println("Please select what you would like from the following:");
				System.out.println("1| check account history");
				System.out.println("2| close an account");
				System.out.println("0| go back");

				int choice = -1;

				if (in.hasNextInt()) {
					choice = in.nextInt();
				} else {
					in.next();
				}

				switch (choice) {
				case 1:
					try {
						System.out.print("Please enter the account number: ");
						CustomArrayList<AccountHistory> list = manager.viewTransactionHistory(in.nextInt());

						if (!(list == null)) {
							if (list.isEmpty()) {
								System.out.println("Error could not find account");
							} else {
								for (int i = 0; i < list.size(); i++) {
									System.out.println(list.get(i).toString());
								}
							}
						} else {
							System.out.println("Error could not find account");
						}

						break;
					} catch (InputMismatchException e) {
						logger.warn("Invalid input");
						System.out.println("Invalid input");
					}
				case 2:
					try {
						System.out.println("What account number would you like to close?");
						int accountNumber = in.nextInt();
						System.out.println(manager.closeAccount(accountNumber));
					} catch (InputMismatchException e) {
						logger.warn("Invalid input");
						System.out.println("Invalid input");
					}
				case 0:
					quit = true;
					break;
				default:
					System.out.println("Error: invalid selection");
				}

			} while (quit != true);
		} else {
			System.out.println("wrong username/password");
		}

	}

	private static void createNewUser(Scanner in) {
		System.out.println("Please enter your first name");
		String firstName = in.next();
		System.out.println("Please enter your last name");
		String lastName = in.next();
		String name = firstName + " " + lastName;
		System.out.println("Please enter a username. No spaces allowed");
		String username = in.next();
		System.out.println("Please enter a password. No spaces allowed");
		String password = in.next();

		NewUserServiceImpl newUser = new NewUserServiceImpl();
		System.out.println(newUser.createUser(username, password, name));
	}

}
