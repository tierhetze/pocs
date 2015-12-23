package com.outtribe.dpost;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

/**
 * 
 * @author peterk
 * 
 * You allowed to use this client without any restrictions.
 * 
 * Requirements to the client:
 * 
 * This is simple HTTP JSON client.
 * 
 * 1. depends on JSONObject: http://www.json.org/javadoc/org/json/JSONObject.html
 * 2. depends on Apache http client  http://hc.apache.org/
 * 3. depends on Apache http core    http://hc.apache.org/
 * 
 *
 * 
 *
 */
public class DpostClient {
	private String host; 
	private int port;
	
	private static DpostClient client;
	
	private DpostClient(){
		//TODO: please externalize the configuration of the DPost host and port
		// for simplicity I hardcoded that
		this.host = "localhost";
		this.port = 9000;
		
	}
	
	//use singleton instance of DPost: (use your framework singleton instead: Spring, JSF bean, etc...)
	public static DpostClient getDpost(){
		if(client==null){
			synchronized (DpostClient.class) {
				if(client==null){
					client = new DpostClient();
				}
			}
		}
		return client;
	}
	
	/**
	 * 
	 * @param subjMap   - parameters of subject
	 * @param bodyMap   - parameters of body
	 * @param toMail    - user(ricipient) e-mail
	 * @param template  - template name
	 * @param lang      - template language
	 * @return true if e-mail succeed to reach DPost server
	 */
	public boolean sendMail(Map<String, String> subjMap, Map<String, String> bodyMap, String toMail, String template, String lang){
		
		/*Note obligatory /dpost URL ending*/
		try {
	        HttpPost request = new HttpPost("http://"+host+":"+port +"/dpost");
	        JSONObject jo = new JSONObject(); 
	        jo.put("subjMap", new JSONObject(subjMap));
	        jo.put("bodyMap", new JSONObject(bodyMap));
	        jo.put("toMail", toMail);
	        jo.put("template", template);
	        jo.put("lang", lang);
	        
	        System.out.println("request="+ jo.toString());
	        
	        StringEntity params = new StringEntity(jo.toString());
	        request.setHeader("Content-type", "application/json; charset=UTF-8");
	        
	        request.setEntity(params);
	        HttpClient httpClient = new DefaultHttpClient();
	        HttpResponse response = httpClient.execute(request);
	        httpClient.getConnectionManager().closeExpiredConnections();
            System.out.println("response="+ response);
            
            if(response!=null){
            	if(response.getStatusLine()!=null){
		            int code = response.getStatusLine().getStatusCode();
		            if(code==200){
		            	return true;
		            }
            	}
            }
            
            
	    }catch (Exception ex) {
	    	
	        ex.printStackTrace();
	    	
	    } 
		return false;
	}
	
	
	public static void main(String[] args) {
		//imagine this is your JSP/JSF/etc file:
		//connect imaginary DB and take parameters/variables from there!
		
		//actual e-mail sending:
		
		Map<String, String> subjMap = new HashMap<>();
		//loop data taken from DB and fill the map
		subjMap.put("username", "Peter");
		
		Map<String, String> bodyMap = new HashMap<>();
		//loop data taken from DB and fill the map
		bodyMap.put("username", "Peter");
		bodyMap.put("product", "Samsung Note II");
		bodyMap.put("salesemail", "peter@peter.com");
		
		//use singleton instance of DPost: (use your framework singleton instead: Spring, JSF bean, etc...)
		DpostClient  client = DpostClient.getDpost();
		client.sendMail(subjMap, bodyMap, "myclient@gmail.com", "welcome", "de-DE");
	}
	
	
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		
	}

}
