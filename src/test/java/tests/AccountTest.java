package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;

import org.eclipse.persistence.internal.nosql.adapters.mongo.MongoConnection;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.DB;
import com.phpm.model.Account;
import com.phpm.model.Transaction;
import com.phpm.model.User;
import com.phpm.services.BankServices;
import com.phpm.util.TransactionType;
import com.phpm.util.UserType;

public class AccountTest {

	private static EntityManager em;
	private static DB db;
	
	private EntityTransaction tx;
	
	private String id;
	private Account account1;
	private Account account2;
	
	@BeforeClass public static void setUpPU() {
		em = Persistence.createEntityManagerFactory("mongodb").createEntityManager();
	}
	
	/**
	 * Attention: EclipseLink requires an active transaction although MongoDB itself
	 * <b>DOES NOT</b> transaction at all!
	 */
	@Before public void setUp() {
		tx = em.getTransaction();
		tx.begin();

		db = ((MongoConnection)em.unwrap(javax.resource.cci.Connection.class)).getDB();
        db.dropDatabase();
        
		account1 = new Account();
		account1.setAccountNumber(1234321);
		account1.setBalance(100);
		
		account2 = new Account();
		account2.setAccountNumber(1234567);
		account2.setBalance(10);
		
		em.persist(account1);
		em.persist(account2);
		em.flush();
		id = account1.getId();
		
//		tx.commit();
		
	}

	/**
	 * Uses entity manager primary key lookup.
	 */
	@Test public void should_find_by_primary_key() {
		Account account = em.find(Account.class, id);
		assertAccount(account);
	}
	
	
	/**
	 * Uses JPQL query (that gets translated to native MongoDB query.
	 */
	@Test public void verifyTransfer() {
		
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//		BankServices bs = new BankServices();
//		bs.executeTrasfer(account1, account2, 45);
		Date date = new Date();
//		tx = em.getTransaction();
//		tx.begin();
		
		Transaction transactionSender = new Transaction();
		transactionSender.setAccountNumber(account1.getAccountNumber());
//		transactionSender.setDate(date);
		transactionSender.setType(TransactionType.TRANSFER);
		transactionSender.setValue(-45);
		
		account1.setBalance(account1.getBalance() - 45);
		
		Transaction transactionReceiver = new Transaction();
		transactionReceiver.setAccountNumber(account2.getAccountNumber());
//		transactionReceiver.setDate(date);
		transactionReceiver.setType(TransactionType.TRANSFER);
		transactionReceiver.setValue(45);
		
		account2.setBalance(account2.getBalance() + 45);
		
		em.persist(transactionSender);
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 1");
		em.persist(transactionReceiver);
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 2");
//		em.persist(sender);
//		em.persist(receiver);
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> em.getFlushMode() = " + em.getFlushMode());
//		em.setFlushMode(FlushModeType.COMMIT);
		
		em.flush();
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 4");
//		tx.commit();
		
		Transaction debitTransaction = 
				(Transaction) em.createQuery("Select t from Transaction t where t.accountNumber = 1234321").getSingleResult();
		assertEquals(-45.0, debitTransaction.getValue(), 0);
		
		Transaction creditTransaction = 
				(Transaction) em.createQuery("Select t from Transaction t where t.accountNumber = 1234567").getSingleResult();
		assertEquals(45.0, creditTransaction.getValue(), 0);
		
		Account debitAccount = 
				(Account) em.createQuery("Select a from Account a where a.accountNumber = 1234321").getSingleResult();
		assertEquals(65.0, debitAccount.getBalance(), 0);
		
		Account creditAccount = 
				(Account) em.createQuery("Select a from Account a where a.accountNumber = 1234567").getSingleResult();
		assertEquals(55.0, creditAccount.getBalance(), 0);
	}

	/**
	 * Uses native MongoDB query (which consists of the full find command like used in the
	 * Mongo shell, not only the query string itself.
	 * <p>
	 * Note that EclipseLink converts the names of collections field to upper case.
	 */
	@Test public void should_find_by_primary_with_native_query() {
		// when
		Account account = (Account)em
				.createNativeQuery("db.ACCOUNT.findOne({_id: \"" + id + "\"})", Account.class)
				.getSingleResult();
		
		assertAccount( account );
	}
	
	/**
	 * Uses native MongoDB query (which consists of the full find command like used in the
	 * Mongo shell, not only the query string itself.
	 * <p>
	 * Note that EclipseLink converts the names of collections field to upper case.
	 */
	public void resetData() {
		
		int N = 10;
		List<User> users = new ArrayList<User>();
		List<Account> accounts = new ArrayList<Account>();
		
		tx = em.getTransaction();
		tx.begin();
		
        db.dropDatabase();

		User u = new User();
		u.setName("John Smith");
		u.setPassword("admin");
		u.setType(UserType.ADMIN);
		u.setUsername("admin");
		em.persist(u);
		
		for (int i = 0; i < N; i++) {
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
		tx = em.getTransaction();
		tx.begin();

		for (int i = 0; i < 10 * N; i++) {
			Account a = new Account();
			a.setAccountNumber(i);
			a.setBalance( Math.rint(Math.random() * 1000000.0) / 100.0 );
			a.setUserId(users.get( (int) (users.size() * Math.random())).getId());
			em.persist(a);
			accounts.add(a);
		}
		em.flush();
		tx.commit();
//		tx = em.getTransaction();
//		tx.begin();
//        
//		for (int i = 0; i < 50 * N; i++) {
//			Transfer t = new Transfer();
//			t.setAccountNumber(i);
//			t.setValue( Math.rint(Math.random() * 100000.0) / 100.0 );
//			t.setAccountNumber(accounts.get( (int) (accounts.size() * Math.random())).getAccountNumber());
//			t.setType(i % 2 == 0 ? TransferType.CREDIT: TransferType.DEBIT);
//			em.persist(t);
//		}
//		em.flush();
//		tx.commit();
		tx = em.getTransaction();
		tx.begin();
        
		for (int i = 0; i < 50 * N; i++) {
			Transaction t = new Transaction();
			t.setAccountNumber(i);
			t.setAccountNumber(accounts.get( (int) (accounts.size() * Math.random())).getAccountNumber());
			t.setType(i % 3 == 0 ? TransactionType.DEPOSIT : i % 2 == 0 ? TransactionType.WITHDRAW : TransactionType.TRANSFER);
			
			if ( t.getType() == TransactionType.WITHDRAW ) {
				t.setValue( -1 * Math.rint(Math.random() * 500.0) );
			} else if ( t.getType() == TransactionType.TRANSFER ) {
				t.setValue( i % 2 == 0 ? -1 : 1 * Math.rint(Math.random() * 500.0) );
			} else {
				t.setValue( Math.rint(Math.random() * 500.0) );
			}
			
			em.persist(t);
		}
		em.flush();
	}
	
	@After public void tearDown() {
		tx.commit();
	}
	
	@AfterClass public static void closeEntityManager() {
		if (em != null) {
			em.close();
		}
	}
	
	private static void assertAccount(Account account) {
		assertNotNull( account );
		assertEquals( 1234321, account.getAccountNumber());
		assertEquals( 100, account.getBalance(),0);	
	}
	
}