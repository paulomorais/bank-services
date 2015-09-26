package com.phm.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.phm.dao.UserDAO;

/**
 * Restfull API for mock data creation
 * @author Paulo
 *
 */
@Path("/mock")
@Produces(MediaType.TEXT_PLAIN)
public class MockDataResource {
	
	/**
	 * Method HTTP GET to create mock data (Users, Accounts and Transactions)
	 * @param users Number of Users to create
	 * @param accounts Number of Accounts to create per user (Accounts created on database = #Users x #Accounts)
	 * @param transactions Number of Transactions to create per account (Number of Transactions created on 
	 * database = #Users x #Accounts x #Transactions)
	 */
	@GET
    public String createData(@QueryParam("users") int nUsers, @QueryParam("accounts") int nAccounts, 
    		@QueryParam("transactions") int nTransactions) {
		
		UserDAO userDAO = new UserDAO();
		userDAO.createMockData(nUsers, nAccounts, nTransactions);
		return "OK";
    }

}
