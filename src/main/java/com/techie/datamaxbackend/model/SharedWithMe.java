package com.techie.datamaxbackend.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;

@Entity
public class SharedWithMe {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Integer sharedId;
	String filePath;
	String sharedToUserEmail;

	Integer sharedByUserId;
	Integer sharedToUserId;
	String sharedBySecretKey;

	Date sharedOn;

	@PrePersist
	void addDateTime() {
		this.sharedOn = new Date();
	}

	
	public SharedWithMe() {
		// TODO Auto-generated constructor stub
	}

	public SharedWithMe(Integer sharedId, String filePath, String sharedToUserEmail, Integer sharedByUserId,
			Integer sharedToUserId, String sharedBySecretKey) {
		super();
		this.sharedId = sharedId;
		this.filePath = filePath;
		this.sharedToUserEmail = sharedToUserEmail;
		this.sharedByUserId = sharedByUserId;
		this.sharedToUserId = sharedToUserId;
		this.sharedBySecretKey = sharedBySecretKey;
	}

	public Map<String, Object> getMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("sharedId", sharedId);
		map.put("filePath", filePath);
		map.put("sharedToUserEmail", sharedToUserEmail);
		map.put("sharedByUserId", sharedByUserId);
		map.put("sharedToUserId", sharedToUserId);
		map.put("sharedBySecretKey", sharedBySecretKey);
		map.put("sharedOn", sharedOn);
		return map;
	}


	public Integer getSharedId() {
		return sharedId;
	}

	public void setSharedId(Integer sharedId) {
		this.sharedId = sharedId;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Integer getSharedByUserId() {
		return sharedByUserId;
	}

	public void setSharedByUserId(Integer sharedByUserId) {
		this.sharedByUserId = sharedByUserId;
	}

	public Integer getSharedToUserId() {
		return sharedToUserId;
	}

	public void setSharedToUserId(Integer sharedToUserId) {
		this.sharedToUserId = sharedToUserId;
	}

	public String getSharedBySecretKey() {
		return sharedBySecretKey;
	}

	public void setSharedBySecretKey(String sharedBySecretKey) {
		this.sharedBySecretKey = sharedBySecretKey;
	}

	public Date getSharedOn() {
		return sharedOn;
	}

	public void setSharedOn(Date sharedOn) {
		this.sharedOn = sharedOn;
	}

}
