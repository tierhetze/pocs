package controllers;

import static play.data.Form.form;
import models.Computer;
import models.ComputerProposal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import services.StorageService;
import views.html.createForm;
import views.html.createProposalForm;
import views.html.editForm;
import views.html.list;
import views.html.listProposals;

/**
 * Manage a database of computers
 */
@org.springframework.stereotype.Controller
public class Application extends Controller{
    @Autowired
	public StorageService storage;
	
	/**
     * This result directly redirect to application home.
     */
    public Result GO_HOME = redirect(
        routes.Application.list(0, "name", "asc", "")
    );
    
    public Result GO_PROPOSALS = redirect(
        routes.Application.listProposals(0, "name", "asc", "")
    );
    
    /**
     * Handle default path requests, redirect to computers list
     */
    public Result index() {
        return GO_HOME;
    }
    
    public Result proposals() {
        return GO_PROPOSALS;
    }

    /**
     * Display the paginated list of computers.
     *
     * @param page Current page number (starts from 0)
     * @param sortBy Column to be sorted
     * @param order Sort order (either asc or desc)
     * @param filter Filter applied on computer names
     */
    @Transactional(readOnly=true)
    public Result list(int page, String sortBy, String order, String filter) {
    	return ok(
            list.render(
                Computer.page(page, 10, sortBy, order, filter, storage.entityManager),
                sortBy, order, filter
            )
        );
    }
    
    @Transactional(readOnly=true)
    public Result listProposals(int page, String sortBy, String order, String filter) {
    	return ok(
    		listProposals.render(
                ComputerProposal.page(page, 10, sortBy, order, filter, storage.entityManager),
                sortBy, order, filter
            )
        );
    }
    
    /**
     * Display the 'edit form' of a existing Computer.
     *
     * @param id Id of the computer to edit
     */
    @Transactional(readOnly=true)
    public Result edit(Long id) {
        Form<Computer> computerForm = form(Computer.class).fill(
        		storage.findComputerById(id)
        );
        return ok(
            editForm.render(id, computerForm, storage)
        );
    }
    
    /**
     * Handle the 'edit form' submission 
     *
     * @param id Id of the computer to edit
     */
    @Transactional
    public Result update(Long id) {
        Form<Computer> computerForm = form(Computer.class).bindFromRequest();
        if(computerForm.hasErrors()) {
            return badRequest(editForm.render(id, computerForm, storage));
        }
        Computer c = computerForm.get();
        storage.updateComputer(c, id);
        flash("success", "Computer " + computerForm.get().name + " has been updated");
        return GO_HOME;
    }
    
    
    
    
    
    /**
     * Display the 'new computer form'.
     */
    @Transactional(readOnly=true)
    public Result create() {
        Form<Computer> computerForm = form(Computer.class);
        return ok(
            createForm.render(computerForm, storage)
        );
    }
    
    
    /**
     * Handle the 'new computer form' submission 
     */
    @Transactional
    public Result save() {
        Form<Computer> computerForm = form(Computer.class).bindFromRequest();
        if(computerForm.hasErrors()) {
            return badRequest(createForm.render(computerForm, storage));
        }
        Computer c = computerForm.get();
        storage.saveComputer(c);
        flash("success", "Computer " + computerForm.get().name + " has been created");
        return GO_HOME;
    }
    
    
    
    @Transactional(readOnly=true)
    public Result proposal() {
        Form<ComputerProposal> computerForm = form(ComputerProposal.class);
        return ok(
            createProposalForm.render(computerForm, storage)
        );
    }
    

    
    
    /**
     * Handle computer deletion
     */
    @Transactional
    public Result delete(Long id) {
    	Computer c = storage.findComputerById(id);
    	storage.delete(c);
        flash("success", "Computer has been deleted");
        return GO_HOME;
    }
    

}
            
