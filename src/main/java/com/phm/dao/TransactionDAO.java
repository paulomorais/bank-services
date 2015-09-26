package com.phm.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;

import com.phm.model.Account;
import com.phm.model.Transaction;
import com.phm.util.TransactionType;


/**
 * @author Paulo
 * Class that provide access to Transactions on the database
 */
public class TransactionDAO extends GenericDAO<Transaction> {
	
	public TransactionDAO() {
		super();
		c = Transaction.class;
	}

	/**
	 * List of Transactions considering the filters parameters
	 * @param accountNumber Account number to use on filter (required)
	 * @param startAt Start index to filter from (Pages)
	 * @param size Size of the page to filter (default = 10)
	 * @param startDate Start date to filter from
	 * @param endDate Final date to filter from
	 * @return List of Transactions
	 */
	public List<Transaction> list(long accountNumber, int startAt, int size, String startDate, String endDate) {
		
		StringBuffer str = new StringBuffer("SELECT t FROM Transaction t WHERE t.accountNumber = ");
		str.append(accountNumber);
		
		Date startDateDt = parseStringToDate(startDate);
		Date endDateDt = parseStringToDate(endDate);
		
		if (startDateDt != null && endDateDt != null) {
			str.append(" AND t.date BETWEEN '" + startDate + " 00:00:00' AND '" + endDate + " 11:59:59'");
		} else if (startDateDt != null) {
			str.append(" AND t.date >= '" + startDate + " 00:00:00'");
		} else if (endDateDt != null) {
			str.append(" AND t.date <= '" + endDate + " 11:59:59'");
		}
		str.append(" ORDER BY t.date DESC");
		
		TypedQuery<Transaction> query = em.createQuery(str.toString(), Transaction.class);
		
		if (startAt >= 0) {
			query.setFirstResult(startAt);
		}
		if (size >= 1) {
			query.setMaxResults(size);
		} else {
			query.setMaxResults(10);
		}
		
		List<Transaction> results = query.getResultList();
		return results;
	}
	
	/**
	 * Row count method to use on web application (if data-table on web application requires row count)
	 * @param accountNumber Account number to use on filter (required)
	 * @param startDate Start date to filter from
	 * @param endDate Final date to filter from
	 * @return size of result of the query
	 */
	public long rowCountlist(long accountNumber, String startDate, String endDate) {
		
		StringBuffer str = new StringBuffer("SELECT COUNT(t) FROM Transaction t WHERE t.accountNumber = ");
		str.append(accountNumber);
		
		Date startDateDt = parseStringToDate(startDate);
		Date endDateDt = parseStringToDate(endDate);
		
		if (startDateDt != null && endDateDt != null) {
			str.append(" AND t.date BETWEEN '" + startDate + " 00:00:00' AND '" + endDate + " 11:59:59'");
		} else if (startDateDt != null) {
			str.append(" AND t.date >= '" + startDate + " 00:00:00'");
		} else if (endDateDt != null) {
			str.append(" AND t.date <= '" + endDate + " 11:59:59'");
		}
		
		return em.createQuery(str.toString(), Long.class).getSingleResult().longValue();
	}
	
	
	/**
	 * Method to execute on transfer transaction between accounts
	 * @param transaction Transaction object with value and account number of sender
	 * @param receiverAccount Account number of the receiver of the transaction
	 * @return Transaction of the sender filled in after commit on the database
	 */
	public Transaction executeTrasfer(Transaction transaction, long receiverAccount) {
		
		double value = transaction.getValue();
		
		AccountDAO accountDAO = new AccountDAO();
		Date date = new Date();
		
		Account sender = accountDAO.fetch(transaction.getAccountNumber());
		Account receiver = accountDAO.fetch(receiverAccount);
		
		Transaction transactionSender = new Transaction();
		transactionSender.setAccountNumber(transaction.getAccountNumber());
		transactionSender.setDate(date);
		transactionSender.setType(TransactionType.TRANSFER);
		transactionSender.setValue(-value);
		
		sender.setBalance(sender.getBalance() - value);
		
		Transaction transactionReceiver = new Transaction();
		transactionReceiver.setAccountNumber(receiver.getId());
		transactionReceiver.setDate(date);
		transactionReceiver.setType(TransactionType.TRANSFER);
		transactionReceiver.setValue(value);
		
		receiver.setBalance(receiver.getBalance() + value);
		
		try {
			em.getTransaction().begin();
			em.merge(sender);
			em.merge(receiver);
			em.persist(transactionSender);
			em.persist(transactionReceiver);
			em.getTransaction().commit();
	    } catch (RollbackException ex) {
	    	throw new EntityExistsException(ex);
	    }
		return transactionSender;
	}
	
	/**
	 * Method used to validate if data parameters passed translate to real date before execute queries
	 * @param strDate String formated date
	 * @return Date object parsed from string attribute
	 */
	private Date parseStringToDate(String strDate){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return strDate == null ? null : df.parse(strDate);
		} catch (ParseException e) {
			return null;
		}
	}
}
