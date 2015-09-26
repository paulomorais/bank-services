package com.phm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Paulo
 * Entity used to represent an Account
 */
@Entity
@Table(name="account")
@XmlRootElement
public class Account {

	/**
	 * Account ID represent the Account Number
	 */
	@Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
	private long id;
	
	/**
	 * Represent the current balance of the Account
	 */
	@Column(name="balance", nullable = false)
	private double balance;
	
	/**
	 * User owner of the Account
	 */
	@ManyToOne
    @JoinColumn(name="id_usuario")
	private User user;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
}
