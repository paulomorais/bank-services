package com.phm.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.phm.dao.TransactionDAO;
import com.phm.model.Transaction;
import com.phm.util.TransactionType;

/**
 * Restfull API for execution of transfers between accounts
 * @author Paulo
 *
 */
@Path("/transfer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransferResource {
	
	private TransactionDAO dao = new TransactionDAO();
	
	/**
	 * Method HTTP POST to execute a Transfer between Accounts (creates two Transactions and updates both Accounts)
	 * @param transaction Transaction object with value and account number of sender
	 * @param receiverAccount Account number of the receiver of the transaction
	 * @return Transaction of the sender filled in after commit on the database
	 */
	@POST
	@Path("/{receiverAccount}")
	public Transaction insertTransaction(@PathParam("receiverAccount") long receiverAccount, Transaction transaction){
		
		if (transaction.getType() == TransactionType.TRANSFER){
			return dao.executeTrasfer(transaction, receiverAccount);
		}
		return null;
	}
	
}
