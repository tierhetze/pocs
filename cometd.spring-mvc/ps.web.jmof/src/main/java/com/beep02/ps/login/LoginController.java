package com.beep02.ps.login;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.beep02.ps.user.User;


/**

@author Vladimir Nabokov

Generally see how implement controllers here:
http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/mvc.html#mvc-controller

 **/
@Controller

public class LoginController {
	
	
	/** see what handler arguments and return types supported here
	 * http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/mvc.html#mvc-ann-requestmapping-arguments
	 */
	@RequestMapping(value="/dashboard", method = RequestMethod.GET)
	public String printWelcome(ModelMap model, Principal principal ) {
        String username = principal.getName();
        
        User user = new User();
        //utility method
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        user.setIp(request.getRemoteAddr());
        user.setUsername(username);
        user.setLoginTime(System.currentTimeMillis());
        
        model.addAttribute("user", user);
        
        String testusersfrom =  (String)request.getSession().getAttribute("testusersfrom");
        String testusersto   =  (String)request.getSession().getAttribute("testusersto");
        
        if(testusersfrom!=null && testusersto!=null){
        	 model.addAttribute("testusersfrom", testusersfrom);
        	 model.addAttribute("testusersto", testusersto);
        	 return "multi-user-emulation-test";
        }else{
        	 return "dashboard";
        }
    }
 
	@RequestMapping(value="/login", method = RequestMethod.GET)
	public String login(ModelMap model) {
        return "login";
    }
	
	@RequestMapping(value="/loginfailed", method = RequestMethod.GET)
	public String loginerror(ModelMap model) {
        model.addAttribute("error", "true");
		return "login";
 
	}
	
	@RequestMapping(value="/logout", method = RequestMethod.GET)
	public String logout(ModelMap model) {
        return "login";
    }
	   
	   
	   
	   
}
