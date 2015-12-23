package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints;

/**
 * 
 * @author Peter Romanoff
 *
 */
/**
 * Company entity managed by JPA
 */
@Entity 
public class Company {
    @Id
    public Long id;
    
    @Constraints.Required
    public String name;
}

