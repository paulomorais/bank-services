package com.phm.dao;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import com.phm.model.Account;
import com.phm.model.User;

/**
 * @author Paulo
 * Class that provide access to Accounts on the database
 */
public class AccountDAO extends GenericDAO<Account> {

	public AccountDAO() {
		super();
		c = Account.class;
	}
	
	/**
	 * List of Accounts considering the filters parameters
	 * @param username username of the Account to filter
	 * @param startAt Start index to filter from (Pages)
	 * @param size Size of the page to filter (default = 10)
	 * @return List of Accounts
	 */
	public List<Account> list(String username, int startAt, int size) {
		
		StringBuffer str = new StringBuffer("SELECT DISTINCT a FROM Account a");
		if (username != null){
			str.append(" LEFT JOIN a.user u WHERE u.username = '" + username + "'");
		}
		TypedQuery<Account> query = em.createQuery(str.toString(), Account.class);
		
		if (startAt >= 0) {
			query.setFirstResult(startAt);
		}
		if (size >= 1) {
			query.setMaxResults(size);
		} else {
			query.setMaxResults(10);
		}
		
		return query.getResultList();
	}
	
	/**
	 * Row count method to use on web application (if data-table on web application requires row count)
	 * @param username username of the Account to filter
	 * @return size of result of the query
	 */
	public long rowCountlist(String username) {
		
		StringBuffer str = new StringBuffer("SELECT DISTINCT COUNT(a) FROM Account a");
		
		if (username != null){
			str.append(" LEFT JOIN a.user u WHERE u.username = '" + username + "'");
		}
		
		return em.createQuery(str.toString(), Long.class).getSingleResult().longValue();
	}
	
	/**
	 * Retrieve one Account from database
	 * @param accountNumber Account Number to retrieve
	 * @return Account object
	 */
	public Account find(long accountNumber) {
		
		return em.find(Account.class, accountNumber);
	}
	
	/**
	 * Method used to retrieve the list of accounts from one user
	 * @param username username of the Account to filter
	 * @return List of Accounts filtered by username
	 */
	public List<Account> findByUsername(String username) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Account> criteriaQuery = criteriaBuilder.createQuery(Account.class);
		Root<Account> accountRoot = criteriaQuery.from(Account.class);
		TypedQuery<Account> query = null;

		Join<Account, User> joinAccoutUser = accountRoot.join("user", JoinType.LEFT);
		criteriaQuery.where(criteriaBuilder.equal(joinAccoutUser.<String>get("username"), username));
		
		query = em.createQuery(criteriaQuery);
		
		return query.getResultList();
	}
}
