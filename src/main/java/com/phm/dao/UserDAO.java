package com.phm.dao;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.phm.model.Account;
import com.phm.model.Transaction;
import com.phm.model.User;
import com.phm.util.TransactionType;
import com.phm.util.UserType;

/**
 * @author Paulo
 * Class that provide access to Users on the database
 */
public class UserDAO extends GenericDAO<User> {
	
	public UserDAO() {
		super();
		c = User.class;
	}
	
	/* 
	 * Override generic create to encrypt attribute password
	 * 
	 */
	@Override
	public void create(User t) {
		t.setPassword( encrypt( t.getPassword() ));
		super.create(t);
	}
	
	/**
	 * List of Users considering the filters parameters
	 * @param startAt Start index to filter from (Pages)
	 * @param size Size of the page to filter (default = 10)
	 * @return List of Users
	 */
	public List<User> list(int startAt, int size) {
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<User> query = cb.createQuery(User.class);
	    TypedQuery<User> typedQuery = em.createQuery(query);
		
		if (startAt >= 0) {
			typedQuery.setFirstResult(startAt);
		}
		if (size >= 0) {
			typedQuery.setMaxResults(size);
		} else {
			typedQuery.setMaxResults(10);
		}
		
		List<User> users = typedQuery.getResultList();
		return users;
	}
	
	/**
	 * Row count method to use on web application (if data-table on web application requires row count)
	 * @return size of result of the query
	 */
	public long rowCountlist() {
		return em.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult().longValue();
	}
	
	/**
	 * Method used to retrieve User from attribute username (unique)
	 * @param username username of the User to filter
	 * @return User
	 */
	public User findByUsername(String username) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
		Root<User> userRoot = criteriaQuery.from(User.class);
		Query query = null;
		User userGot = null;

		criteriaQuery.where(criteriaBuilder.equal(userRoot.get("username"), 
				criteriaBuilder.parameter(String.class, "username")));
		
		query = em.createQuery(criteriaQuery);
		query.setParameter("username", username);

		try {
			userGot = (User) query.getSingleResult();
		} catch (Exception e) {
		}
		
		return userGot;
	}
	
	/**
	 * Method used to validate login information (username and password)
	 * @param user User object filled in with username and password attributes
	 * @return User that match the login conditions
	 */
	public User login(User user) {
		
		StringBuffer str = new StringBuffer("SELECT u FROM User u WHERE u.username = '");
		str.append(user.getUsername() + "' AND u.password = '");
		str.append( encrypt(user.getPassword()) + "'");
		
		TypedQuery<User> query = em.createQuery(str.toString(), User.class);
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
		}
		return new User();
	}
	
	
	/**
	 * Method created only to facilitate the creation of mock data on system
	 * @param nUsers Number of Users to create
	 * @param nAccounts Number of Accounts to create per user (Accounts created on database = #Users x #Accounts)
	 * @param nTransactions Number of Transactions to create per account (Number of Transactions created on 
	 * database = #Users x #Accounts x #Transactions)
	 */
	public void createMockData(int nUsers, int nAccounts, int nTransactions) {
		try {
			em.getTransaction().begin();
			em.createQuery("DELETE FROM Transaction").executeUpdate();
			em.createQuery("DELETE FROM Account").executeUpdate();
			em.createQuery("DELETE FROM User").executeUpdate();
			em.getTransaction().commit();
	    } catch (RollbackException ex) {
	    	throw new EntityExistsException(ex);
	    }	
		
		List<Account> accounts = new ArrayList<Account>();
		
		User user = new User();
		user.setName("Nelson Mandela");
		user.setPassword("admin");
		user.setType(UserType.ADMIN);
		user.setUsername("nelson");
		create(user);
		
		int counter = 0;
		int counterLimit = 10000;
		try {
			em.getTransaction().begin();
			for (int u = 1; u <= nUsers; u++) {
				user = new User();
				user.setName(getRandomName());
				user.setPassword(encrypt( "mock" + u));
				user.setType(UserType.CLIENT);
				user.setUsername("mock" + u);
				em.persist(user);
				counter++;
				System.err.println("create user: " + u);
				if (counter > counterLimit){
//					em.getTransaction().commit();
//					em.getTransaction().begin();
					em.flush();
					em.clear();
					counter = 0;
				}
				
				for (int a = 1; a <= nAccounts; a++) {
					Account account = new Account();
					account.setBalance( 1000 );
					account.setUser(user);
					em.persist(account);
					counter++;
					accounts.add(account);
					if (counter > counterLimit){
//						em.getTransaction().commit();
//						em.getTransaction().begin();
						em.flush();
						em.clear();
						counter = 0;
					}
				}
			}
			em.getTransaction().commit();
//			em.flush();
//			em.clear();
	    } catch (RollbackException ex) {
	    	throw new EntityExistsException(ex);
	    }
		
		em.getTransaction().begin();
		counter = 0;
		int index = 0;
		for (Iterator<Account> iterator = accounts.iterator(); iterator.hasNext();) {
			Account account = (Account) iterator.next();
			
			System.err.println("create transactions for account: " + index++);
			
			try {
//				em.getTransaction().begin();
			
				for (int t = 1; t <= nTransactions; t++) {
					Transaction transaction = new Transaction();
					
					transaction.setAccountNumber( account.getId() );
					transaction.setDate(getDataInThePast( nTransactions - t ));
					double value = Math.rint(Math.random() * 10000) / 100;
					
					if ( Math.rint(Math.random() * 3) % 3 == 0){
						transaction.setType(TransactionType.TRANSFER);
						Account accountReceiver = accounts.get( (int) Math.rint(Math.random() * (accounts.size()-1)) );
						
						transaction.setValue( -value );
						account.setBalance(account.getBalance() - value);
						
						Transaction transactionReceiver = new Transaction();
						transactionReceiver.setAccountNumber(accountReceiver.getId());
						transactionReceiver.setDate(transaction.getDate());
						transactionReceiver.setType(TransactionType.TRANSFER);
						transactionReceiver.setValue(value);
						
						accountReceiver.setBalance(accountReceiver.getBalance() + value);
						em.persist(accountReceiver);
						em.persist(transactionReceiver);
						counter += 2;
						
					} else {
						if (Math.rint(Math.random() * 2) % 2 == 0) {
							transaction.setType(TransactionType.WITHDRAW);
							transaction.setValue( -value );
							account.setBalance(account.getBalance() - value);
						} else {
							transaction.setType(TransactionType.DEPOSIT);
							transaction.setValue( +value );
							account.setBalance(account.getBalance() + value);
						}
					}
					em.persist(transaction);
					em.persist(account);
					counter += 2;
				}
				if (counter > counterLimit){
//					em.getTransaction().commit();
//					em.getTransaction().begin();
					em.flush();
					em.clear();
					counter = 0;
				}
//				em.getTransaction().commit();
		    } catch (RollbackException ex) {
		    	throw new EntityExistsException(ex);
		    }
		}
//		em.flush();
//		em.clear();
		em.getTransaction().commit();
	}
	
	private static String[] names = {
				"Marilyn Monroe", "Abraham Lincoln","Mother Teresa","John F. Kennedy","Martin Luther King","Nelson Mandela","Winston Churchill","Bill Gates","Muhammad Ali","Mahatma Gandhi","Margaret Thatcher","Charles de Gaulle","Christopher Columbus","George Orwell","Charles Darwin","Elvis Presley","Albert Einstein","Paul McCartney","Plato","Queen Elizabeth II","Queen Victoria","John M Keynes","Mikhail Gorbachev","Jawaharlal Nehru","Leonardo da Vinci","Louis Pasteur","Leo Tolstoy","Pablo Picasso","Vincent Van Gogh","Franklin D. Roosevelt","Pope John Paul II","Thomas Edison","Rosa Parks","Aung San Suu Kyi","Lyndon Johnson","Ludwig Beethoven","Oprah Winfrey","Indira Gandhi","Eva Peron","Benazir Bhutto","Desmond Tutu","Dalai Lama","Walt Disney","Neil Armstrong","Peter Sellers","Barack Obama","Malcolm X","J.K.Rowling","Richard Branson","Pele","Angelina Jolie","Jesse Owens","Ernest Hemingway","John Lennon","Henry Ford","Haile Selassie","Joseph Stalin","Lord Baden Powell","Michael Jordon","George Bush jnr","V.Lenin","Ingrid Bergman","Fidel Castro","Oscar Wilde","Coco Chanel","Pope Francis","Amelia Earhart","Adolf Hitler","Sting","Mary Magdalene","Alfred Hitchcock","Michael Jackson","Madonna","Mata Hari","Cleopatra","Grace Kelly","Steve Jobs","Ronald Reagan","Lionel Messi","Babe Ruth","Bob Geldof","Leon Trotsky","Roger Federer","Sigmund Freud","Woodrow Wilson","Mao Zedong","Katherine Hepburn","Audrey Hepburn","David Beckham","Tiger Woods","Usain Bolt","Carl Lewis","Prince Charles","Jacqueline Kennedy Onassis","C.S. Lewis","Billie Holiday","J.R.R. Tolkien","Tom Cruise","Billie Jean King","Anne Frank","Simon Bolivar","Stephen King"
	};
	
	private static String getRandomName() {
		return names[(int)(Math.rint(Math.random() * (names.length-1)))];
	}

	private Date getDataInThePast(int daysBefore) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -daysBefore);
		return c.getTime();
	}
	
	/**
	 * Simple MD5 method to encrypt password
	 * @param value String with password
	 * @return String with password encrypted
	 */
	public synchronized static String encrypt(String value) {
		String returnValue = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(value.getBytes());
			BigInteger hash = new BigInteger(1, md.digest());
			returnValue = hash.toString(16);
			return returnValue;
		} catch (NoSuchAlgorithmException ns) {
			return "";
		}
	}
}
