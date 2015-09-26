package com.phm.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.phm.dao.AccountDAO;
import com.phm.dao.UserDAO;
import com.phm.model.Account;
import com.phm.model.User;
import com.phm.util.UserType;

/**
 * @author Paulo
 * Test Class
 */
public class AccountTest {


	/**
	 * Method to create some data in order to test connection with database
	 */
	@Test public void createMockData() {
		UserDAO userDAO = new UserDAO();
		AccountDAO accountDAO = new AccountDAO();
		int N = 20;
		List<User> users = new ArrayList<User>();
		List<Account> accounts = new ArrayList<Account>();
		
		User u = new User();
		u.setName("John Smith");
		u.setPassword("admin");
		u.setType(UserType.ADMIN);
		u.setUsername("john");
		userDAO.create(u);
		
		for (int i = 0; i < N; i++) {
			u = new User();
			u.setName("User number " + i);
			u.setPassword("user-" + i);
			u.setType(UserType.CLIENT);
			u.setUsername("user-" + i);
			userDAO.create(u);
			users.add(u);
		}

		for (int i = 0; i < 2 * N; i++) {
			Account a = new Account();
			a.setBalance( Math.rint(Math.random() * 1000000.0) / 100.0 );
			a.setUser(users.get( (int) (users.size() * Math.random())));
			accountDAO.create(a);
			accounts.add(a);
		}
	}
	
}