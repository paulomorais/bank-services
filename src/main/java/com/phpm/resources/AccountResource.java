package com.phpm.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.phpm.model.Account;
import com.phpm.model.User;
import com.phpm.services.BankServices;

@Path("/accounts")
public class AccountResource {
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
    public List<Account> getAccounts() {
		return BankServices.getAllAccounts();
    }
	
	@GET
	@Path("/{userId}")
	@Produces(MediaType.APPLICATION_XML)
    public List<Account> getUser(@PathParam("userId") String userId) {
        return BankServices.getAllAccountsByUser(userId);
    }

	@GET
	@Path("/id/{accountId}")
	@Produces(MediaType.APPLICATION_XML)
    public Account getUserByUserId(@PathParam("accountId") String accountId) {
        return BankServices.getAccount(accountId);
    }
}
