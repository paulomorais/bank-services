package com.phm.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.phm.dao.AccountDAO;
import com.phm.model.Account;

/**
 * Restfull API for Account Retrieval
 * @author Paulo
 *
 */
@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountResource {
	
	AccountDAO dao = new AccountDAO();
	
	/**
	 * Method HTTP GET to retrieve a list of accounts
	 * @param username username of the Account to filter
	 * @param startAt Start index to filter from (Pages)
	 * @param size Size of the page to filter (default = 10)
	 * @return List of Accounts
	 */
	@GET
    public List<Account> getAccounts(
    		@QueryParam("username") String username, 
    		@QueryParam("startAt") int startAt, 
    		@QueryParam("size") int size) {
		
		return dao.list(username, startAt, size);
    }
	
	/**
	 * Method HTTP GET to retrieve the row count of a query
	 * @param username username of the Account to filter
	 * @return size of result of the query
	 */
	@GET
	@Path("/rowCount")
	@Produces(MediaType.TEXT_PLAIN)
    public long rowCount(
    		@QueryParam("username") String username) {
		
		return dao.rowCountlist(username);
    }
	
	/**
	 * Method HTTP GET to retrieve one Account
	 * @param accountNumber Account Number to retrieve
	 * @return Account object
	 */
	@GET
	@Path("/{accountNumber}")
    public Account getUser(@PathParam("accountNumber") long accountNumber) {
		
		return dao.find(accountNumber);
    }
}
