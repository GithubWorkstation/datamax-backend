package com.techie.datamaxbackend.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Users {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Integer userId;
	String email;
	String password;
	String fullName;
	String secretKey;
	String rootFolderName;
	
	int access; 
	int status;
	Date addedOn;
	
	public Users() {
	
	}
	

	public Users(String email, String password, String fullName) {
		super();
		this.email = email;
		this.password = password;
		this.fullName = fullName;
	}



	public Users(Integer userId, String email, String password, String fullName, String secretKey, int access,
			int status, Date addedOn) {
		super();
		this.userId = userId;
		this.email = email;
		this.password = password;
		this.fullName = fullName;
		this.secretKey = secretKey;
		this.access = access;
		this.status = status;
		this.addedOn = addedOn;
	}


	public Users(Integer userId, String email, String password, String fullName, String secretKey,
			String rootFolderName, int access, int status, Date addedOn) {
		super();
		this.userId = userId;
		this.email = email;
		this.password = password;
		this.fullName = fullName;
		this.secretKey = secretKey;
		this.rootFolderName = rootFolderName;
		this.access = access;
		this.status = status;
		this.addedOn = addedOn;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getRootFolderName() {
		return rootFolderName;
	}

	public void setRootFolderName(String rootFolderName) {
		this.rootFolderName = rootFolderName;
	}

	public int getAccess() {
		return access;
	}

	public void setAccess(int access) {
		this.access = access;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getAddedOn() {
		return addedOn;
	}

	public void setAddedOn(Date addedOn) {
		this.addedOn = addedOn;
	}
	
}
