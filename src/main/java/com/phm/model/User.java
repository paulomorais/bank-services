package com.phm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.phm.util.UserType;

/**
 * @author Paulo
 * Entity used to represent an User
 */
@Entity
@Table(name="user")
@XmlRootElement
public class User {

	/**
	 * User ID of the User
	 */
	@Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
	private long id;
	
	/**
	 * Username of the User (Unique)
	 */
	@Column(name = "username", nullable = false, unique = false, length=30)
	private String username;
	
	/**
	 * Password of the User, encrypted
	 */
	@Column(name = "password", nullable = false)
	private String password;
	
	/**
	 * Type of User, see @UserType
	 */
	@Column(name="type", nullable = false)
	private UserType type;
	
	/**
	 * Name of the User
	 */
	@Column(name="name", nullable = false)
	private String name;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public UserType getType() {
		return type;
	}
	public void setType(UserType type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
