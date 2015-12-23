package com.activititest.demo;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.activititest.jpa.example.DemoDataGenerator;
/**
 * 
 * @author Vladimir Nabokov
 *
 */
public class Test {
	
	
	static ApplicationContext ctx ;
	static RuntimeService runtimeService;
	static TaskService taskService;
	static HistoryService historyService;
	static RepositoryService repositoryService;
	
	public static void main(String[] args) {

		//initialize context, create test data
		ctx  = new ClassPathXmlApplicationContext("application-context.xml", DemoDataGenerator.class);
		
		final DemoDataGenerator ddGenerator = (DemoDataGenerator)ctx.getBean("demoDataGenerator");
		ddGenerator.init();
		
		
 		runtimeService  = (RuntimeService) ctx.getBean("runtimeService");
 		taskService     = (TaskService)    ctx.getBean("taskService");;
 		historyService  = (HistoryService) ctx.getBean("historyService");;
 		repositoryService = (RepositoryService)ctx.getBean("repositoryService");;
		
		//start thread, that executes the business process
		Thread t = new Thread(){

			@Override
			public void run() {
				
				Map<String, Object>  attributes   = new HashMap<>();
				attributes.put("numberOfDays", 10L);
				attributes.put("startDate", "11-02-2014 12:00");
				attributes.put("vacationMotivation", "Tired...");
				attributes.put("employeeName", "fozzie"); 
				
				ProcessInstance instance = runtimeService.startProcessInstanceByKey("dummyJpaProcess", attributes);
				
				System.out.println("1) worker has submitted vacation request...:"+ attributes+"\n\n\n\n\n");
				
				
                
                
                HistoricProcessInstance  historicProcessInstance = 
                	      historyService.createHistoricProcessInstanceQuery().processInstanceId(instance.getProcessInstanceId()).singleResult();
                	    System.out.println("2) Process instance end time: " + historicProcessInstance.getEndTime()+"\n\n\n\n\n");

           }
		};
		
		t.start();
	}
}
