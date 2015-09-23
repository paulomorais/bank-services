package com.phpm.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.phpm.model.Transaction;
import com.phpm.model.User;
import com.phpm.services.BankServices;

@Path("/transactions")
public class TransactionResource {
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
    public List<User> getTransactions() {
		
		return BankServices.getAllUsers();
    }
	
	@GET
	@Path("/{accountNumber}")
	@Produces(MediaType.APPLICATION_XML)
    public List<Transaction> getTransactionsByAccount(@PathParam("accountNumber") long accountNumber) {
		
        return BankServices.getTransactions(accountNumber);
    }
	
}
