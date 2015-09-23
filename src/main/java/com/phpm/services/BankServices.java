package com.phpm.services;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.phpm.model.Account;
import com.phpm.model.Transaction;
import com.phpm.model.Transfer;
import com.phpm.model.User;
import com.phpm.util.TransactionType;

public class BankServices {
	
	public static EntityManager em = Persistence.createEntityManagerFactory("mongodb").createEntityManager();
	
	public Transaction executeTrasfer(Account sender, Account receiver, double value) {
		
		Date date = new Date();
		
		Transaction transactionSender = new Transaction();
		transactionSender.setAccountNumber(sender.getAccountNumber());
//		transactionSender.setDate(date);
		transactionSender.setType(TransactionType.TRANSFER);
		transactionSender.setValue(-value);
		
		sender.setBalance(sender.getBalance() - value);
		
		Transaction transactionReceiver = new Transaction();
		transactionReceiver.setAccountNumber(receiver.getAccountNumber());
//		transactionReceiver.setDate(date);
		transactionReceiver.setType(TransactionType.TRANSFER);
		transactionReceiver.setValue(value);
		
		receiver.setBalance(receiver.getBalance() + value);
		
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(transactionSender);
		em.persist(transactionReceiver);
//		em.persist(sender);
//		em.persist(receiver);
		em.flush();
		return transactionSender;
	}
	
	
	
	
	public static List<User> getAllUsers() {
		
		Query query = em.createQuery("Select u from User u");
		List<User> users = query.getResultList();
		
		return users;
	}
	
	public static User getUser(String username) {
		
		Query query = em.createQuery("Select u from User u where u.username = '" + username + "'");
		User user = (User)query.getSingleResult();
		
		return user;
	}
	
	public static User getUserById(String userId) {
		
		Query query = em.createQuery("Select u from User u where u.id = '" + userId + "'");
		User user = (User)query.getSingleResult();
		
		return user;
	}
	
	
	
	
	public static List<Account> getAllAccounts() {
		Query query = em.createQuery("Select a from Account a");
		List<Account> accounts = query.getResultList();
		return accounts;
	}
	
	public static List<Account> getAllAccountsByUser(String userId) {
		Query query = em.createQuery("Select a from Account a where a.userId = '" + userId + "'");
		List<Account> accounts = query.getResultList();
		return accounts;
	}
	
	public static Account getAccount(String accountId) {
		Query query = em.createQuery("Select a from Account a where a.id = '" + accountId + "'");
		Account account = (Account)query.getSingleResult();
		return account;
	}
	
	public static List<Transaction> getTransactions(long accountNumber) {
		
		Query query = em.createQuery("Select t from Transaction t where t.accountNumber = " + accountNumber);
		List<Transaction> transactions = query.getResultList();
		
		return transactions;
	}
	
	public static List<Transfer> getTransfers(long accountNumber) {
		
		Query query = em.createQuery("Select t from Transfer t where t.accountNumber = " + accountNumber);
		List<Transfer> transfers = query.getResultList();
		
		return transfers;
	}

	public static void persistElements(List items) {
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		for (Iterator it = items.iterator(); it.hasNext();) {
			Object o = (Object) it.next();
			em.persist(it);
		}
		em.flush();
	}
}
