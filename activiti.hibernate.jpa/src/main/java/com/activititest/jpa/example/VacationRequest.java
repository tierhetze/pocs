package com.activititest.jpa.example;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author Vladimir Nabokov
 *
 */
@Entity
@Table(name = "VACATION_REQUEST")
public class VacationRequest {
	@Id 
    @GeneratedValue 
    private Long id;
	
	
	@Column(length = 32, unique = false)
    private String userName;
	
	@Column(unique = false)
    private Long days;
	
	
	@Column(length = 32, unique = false)
    private String date;
	
	
	@Column(length = 255, unique = false)
    private String reason;


	public Long getId() {
		return id;
	}

    public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public Long getDays() {
		return days;
	}


	public void setDays(Long days) {
		this.days = days;
	}


	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}


	public String getReason() {
		return reason;
	}


	public void setReason(String reason) {
		this.reason = reason;
	}
	
	
	
	
}
