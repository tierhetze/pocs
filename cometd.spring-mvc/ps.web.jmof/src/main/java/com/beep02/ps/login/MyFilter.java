package com.beep02.ps.login;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;


/**

@author Vladimir Nabokov

 **/

public class MyFilter extends WebAuthenticationDetailsSource {

	@Override
	public WebAuthenticationDetails buildDetails(HttpServletRequest context) {
		
		String testusersfrom = context.getParameter("j_testusersfrom");
		String testusersto = context.getParameter("j_testusersto");
		
		if(testusersfrom!=null && !testusersfrom.isEmpty() && testusersto!=null && !testusersto.isEmpty()){
		   context.getSession().setAttribute("testusersfrom", testusersfrom);
		   context.getSession().setAttribute("testusersto", testusersto);
		}
		
		return super.buildDetails(context);
	}
	
	
	

}
