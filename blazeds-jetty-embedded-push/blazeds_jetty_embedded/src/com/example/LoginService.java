package com.example;


/**
 * 
 * @author peter
 * 
 * Note this service is not singleton
 * It is remote service, created per client
 *
 */
public class LoginService {
	
	public Long login(String name){
		return SpringServicesFactory.getUsersRegistry().addNew();
	}
}
