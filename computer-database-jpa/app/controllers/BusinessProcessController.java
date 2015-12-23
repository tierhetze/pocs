package controllers;

import static play.data.Form.form;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import models.ComputerProposal;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import services.StorageService;
import views.html.createProposalForm;


/**
 * 
 * This service is to localize all the process logic entries
 * @author Peter Romanoff
 *
 */
@Component
public class BusinessProcessController extends Controller{
	
	@PersistenceContext
	public EntityManager       entityManager;
	@Autowired
	public RuntimeService      runtimeService;
	@Autowired
	public TaskService         taskService;
	@Autowired
	public HistoryService      historyService;
	@Autowired
	public RepositoryService   repositoryService;
	
	@Autowired
	public StorageService storage;
	
	public Result GO_PROPOSALS = redirect(
        routes.Application.listProposals(0, "name", "asc", "")
    );
	
	
	//UI action
	@Transactional
    public Result saveProposal() {
		
        //get the form
		Form<ComputerProposal> computerForm = form(ComputerProposal.class).bindFromRequest();
        
        //process errors in form
        if(computerForm.hasErrors()) {
            return badRequest(createProposalForm.render(computerForm, storage));
        }
        
        //get data from the form
        ComputerProposal c = computerForm.get();
        
        //this is how I use the business process
        //first: create required variables
        Map<String, Object>  variables   = new HashMap<>();
        variables.put("name", c.name);
        variables.put("introduced", c.introduced);
        variables.put("discontinued", c.discontinued);
		
		//start the BP by start event with required variables
		runtimeService.startProcessInstanceByKey("addCompRequest", variables);
		
		
		//TODO: how to flash error?
		flash("success", "Computer Proposal: " + computerForm.get().name + " has been created");
		
		
        return GO_PROPOSALS;
    }
	
	//UI action
    @Transactional
    public Result approve(Long id) {
    	
    	ComputerProposal c = entityManager.find(ComputerProposal.class, id);
    	
    	if(c == null){
    		//TODO process error
    		flash("failure", "Computer approval entity is not found");
    		return GO_PROPOSALS;
    	}
    	
    	//find the task to approve
    	Task task = taskService.createTaskQuery().taskDefinitionKey("userApproveTask").processVariableValueEquals("computerRequest", c).singleResult();
    	if(task==null){
    		//TODO process error
    		flash("failure", "Computer approval task is not found");
    		return GO_PROPOSALS;
    	}
    	
    	//add variables    	
    	Map<String, Object>  variables   = new HashMap<>();
    	variables.put("id", id);
    	
    	//activate the task
		taskService.complete(task.getId(), variables);
		
    	//TODO: error if task failed
		flash("success", "Computer has been approved");
        return GO_PROPOSALS;
    }
	
    
    
    //BP action
    public ComputerProposal addProposal(String name, Date introduced, Date discontinued) {
      ComputerProposal c = new ComputerProposal();
      c.name=name;
      c.introduced=introduced;
      c.discontinued = discontinued;
      storage.saveComputerProposal(c);
      return c;
    }
    
    //BP action
    public void approveProposal(ComputerProposal p) {
  	     storage.approveProposal(p);
  	}
	
    //BP action
    public boolean automaticalApproval(ComputerProposal p) {
    	  return p.name.startsWith("Apple");
    }
	
	
	
	
	
	

}
