package com.activititest.jpa.example;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

/**
 * Service bean that handles loan requests.
 * 
 * @author Vladimir Nabokov
 */
public class VacationRequestBean  {

  @PersistenceContext
  private EntityManager entityManager;
  

  @Transactional
  public VacationRequest newVacationRequest(String customerName, Long days, String reason, String date) {
	   VacationRequest lr = new VacationRequest();
       lr.setUserName(customerName);
       lr.setDays(days);
       lr.setReason(reason);
       lr.setDate(date);
       entityManager.persist(lr);
       return lr;
  }
  
  public VacationRequest getLoanRequest(Long id) {
       return entityManager.find(VacationRequest.class, id);
  }
}
