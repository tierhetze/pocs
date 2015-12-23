package services;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import models.Company;
import models.Computer;
import models.ComputerProposal;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class StorageService {
	
	@PersistenceContext
	public EntityManager entityManager;
	
	
	@Transactional
	public void addNewComputerStorageRequest(String name, Date introduced, Date discontinued) {
    	ComputerProposal request = new ComputerProposal();
    	request.discontinued = discontinued;
    	request.introduced   = introduced;
    	request.name         = name;
    	entityManager.persist(request);
    }
	
	
	@Transactional
	public Company findCompanyById(Long id) {
        return entityManager.find(Company.class, id);
    }
	
	
	@Transactional
    public Map<String,String> options() {
        @SuppressWarnings("unchecked")
				List<Company> companies = entityManager.createQuery("from Company order by name").getResultList();
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(Company c: companies) {
            options.put(c.id.toString(), c.name);
        }
        return options;
    }
	
	
	@Transactional
    public Computer findComputerById(Long id) {
        return entityManager.find(Computer.class, id);
    }
    
	
	@Transactional
    public void updateComputer(Computer c, Long id) {
        if(c.company.id == null) {
            c.company = null;
        } else {
            c.company = findCompanyById(c.company.id);
        }
        c.id = id;
        entityManager.merge(c);
    }
    
	@Transactional
    public void saveComputer(Computer c) {
        if(c.company.id == null) {
            c.company = null;
        } else {
            c.company = findCompanyById(c.company.id);
        }
        entityManager.persist(c);
        
    }
	
	@Transactional
    public void saveComputerProposal(ComputerProposal c) {
        entityManager.persist(c);
    }
    
    
	@Transactional
    public void delete(Computer c) {
		entityManager.remove(c);
    }

	@Transactional
	public ComputerProposal findProposal(Long id) {
		return entityManager.find(ComputerProposal.class, id);
	}

	@Transactional
	public void approveProposal(ComputerProposal p) {
		Computer c = new Computer();
		c.name=p.name;
		c.discontinued = p.discontinued;
		c.introduced = p.introduced;
		entityManager.persist(c);
		entityManager.remove(p);
	}
	
}
