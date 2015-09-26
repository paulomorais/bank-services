package com.phm.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.annotations.Index;

import com.phm.util.TransactionType;


/**
 * @author Paulo
 * Entity used to represent a Transaction
 */
@Entity
@Table(name="transaction")
@Index(columnNames={"accountNumber", "date"})
@XmlRootElement
public class Transaction {
	
	/**
	 * ID of the Transaction entity
	 */
	@Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
	private long id;
	
	/**
	 * Date when the Transaction was executed
	 */
	@Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date", nullable = false)
	private Date date;
	
	/**
	 * Value of the Transaction executed
	 */
	@Column(name="value", nullable = false)
	private double value;
	
	/**
	 * Type of Transaction, see @TransactionType
	 */
	@Column(name="type", nullable = false)
	private TransactionType type;
	
	/**
	 * Account Number used on this transaction
	 */
	@Column(name="accountNumber", nullable = false)
	private long accountNumber;
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public TransactionType getType() {
		return type;
	}
	public void setType(TransactionType type) {
		this.type = type;
	}
	public long getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(long accountNumber) {
		this.accountNumber = accountNumber;
	}
}
