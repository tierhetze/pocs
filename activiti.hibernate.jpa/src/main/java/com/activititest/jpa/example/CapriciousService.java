package com.activititest.jpa.example;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class CapriciousService implements JavaDelegate{

	@Override
	public void execute(DelegateExecution arg0) throws Exception {
		
		if(false){
			throw new Exception("sometimes shit hits the fan");
		}
		
		
		
	}

}
