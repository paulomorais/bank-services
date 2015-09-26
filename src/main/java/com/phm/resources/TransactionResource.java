package com.phm.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.phm.dao.TransactionDAO;
import com.phm.model.Transaction;

/**
 * Restfull API for Transaction Retrieval
 * @author Paulo
 *
 */
@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionResource {
	
	private TransactionDAO dao = new TransactionDAO();
	
	/**
	 * Method HTTP GET to retrieve a list of transaction
	 * @param accountNumber Account number to use on filter (required)
	 * @param startAt Start index to filter from (Pages)
	 * @param size Size of the page to filter (default = 10)
	 * @param startDate Start date to filter from
	 * @param endDate Final date to filter from
	 * @return List of Transactions
	 */
	@GET
    public List<Transaction> getTransactions(
    		@QueryParam("accountNumber") long accountNumber,
    		@QueryParam("startAt") int startAt, 
    		@QueryParam("size") int size,
    		@QueryParam("startDate") String startDate,
    		@QueryParam("endDate") String endDate) {
        
		return dao.list(accountNumber, startAt, size, startDate, endDate);
    }
	
	/**
	 * Method HTTP GET to retrieve the row count of a query
	 * @param accountNumber Account number to use on filter (required)
	 * @param startDate Start date to filter from
	 * @param endDate Final date to filter from
	 * @return size of result of the query
	 */
	@GET
	@Path("/rowCount")
	@Produces(MediaType.TEXT_PLAIN)
    public long rowCount(
    		@QueryParam("accountNumber") long accountNumber,
    		@QueryParam("startDate") String startDate,
    		@QueryParam("endDate") String endDate) {
		
		return dao.rowCountlist(accountNumber, startDate, endDate);
    }
}
