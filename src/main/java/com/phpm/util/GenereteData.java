package com.phpm.util;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.eclipse.persistence.internal.nosql.adapters.mongo.MongoConnection;

import com.mongodb.DB;
import com.phpm.model.Account;
import com.phpm.model.Transaction;
import com.phpm.model.Transfer;
import com.phpm.model.User;
import com.phpm.services.BankServices;

public class GenereteData {

	private static EntityManager em;
	private static EntityTransaction tx;
	private static List<User> users = new ArrayList<User>();
	private static List<Account> accounts = new ArrayList<Account>();
	
	private static int MAGIC_NUMBER = 2;
	
	public static void main(String[] args) {
		
		em = Persistence.createEntityManagerFactory("mongodb").createEntityManager();
		
		
		tx = em.getTransaction();
		tx.begin();

		DB db = ((MongoConnection)em.unwrap(javax.resource.cci.Connection.class)).getDB();
        db.dropDatabase();
        
        tx.commit();
        
        createUsers(MAGIC_NUMBER);
        createAccounts(10 * MAGIC_NUMBER);
        
        BankServices bs = new BankServices();
		bs.executeTrasfer(accounts.get(1), accounts.get(2), 44);
        
		if (em != null) {
			em.close();
		}
	}
	
	public static void createUsers(int n){
		tx = em.getTransaction();
		tx.begin();

		User u = new User();
		u.setName("John Smith");
		u.setPassword("admin");
		u.setType(UserType.ADMIN);
		u.setUsername("admin");
		em.persist(u);
		
		for (int i = 0; i < n; i++) {
			u = new User();
			u.setName("User number " + i);
			u.setPassword("user-" + i);
			u.setType(UserType.CLIENT);
			u.setUsername("user-" + i);
			em.persist(u);
			users.add(u);
		}
		em.flush();
		tx.commit();
	}
	
	public static void createAccounts(int n){
		tx = em.getTransaction();
		tx.begin();

		for (int i = 0; i < n; i++) {
			Account a = new Account();
			a.setAccountNumber(i);
			a.setBalance( 100 );// Math.rint(Math.random() * 1000000.0) / 100.0 );
			a.setUserId(users.get( (int) (users.size() * Math.random())).getId());
			em.persist(a);
			accounts.add(a);
		}
		em.flush();
		tx.commit();
	}
	
//	public static void createTransfers(int n){
//		tx = em.getTransaction();
//		tx.begin();
//        
//		for (int i = 0; i < n; i++) {
//			Transfer t = new Transfer();
//			t.setAccountNumber(i);
//			t.setValue( Math.rint(Math.random() * 100000.0) / 100.0 );
//			t.setAccountNumber(accounts.get( (int) (accounts.size() * Math.random())).getAccountNumber());
//			t.setType(i % 2 == 0 ? TransferType.CREDIT: TransferType.DEBIT);
//			em.persist(t);
//		}
//		em.flush();
//		tx.commit();
//	}
	
	public static void createTransactions(int n){
		tx = em.getTransaction();
		tx.begin();
        
		for (int i = 0; i < n; i++) {
			Transaction t = new Transaction();
			t.setAccountNumber(i);
			t.setValue( Math.rint(Math.random() * 500.0) );
			t.setAccountNumber(accounts.get( (int) (accounts.size() * Math.random())).getAccountNumber());
			t.setType(i % 2 == 0 ? TransactionType.DEPOSIT : TransactionType.WITHDRAW);
			em.persist(t);
		}
		em.flush();
		tx.commit();
	}
}
