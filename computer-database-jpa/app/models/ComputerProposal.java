package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import play.data.format.Formats;
import play.data.validation.Constraints;

/**
 * 
 * @author Peter Romanoff
 *
 */
@Entity 
@Table(name = "computer_proposal")
@SequenceGenerator(name = "computer_proposal_seq",  sequenceName = "computer_proposal_seq")
public class ComputerProposal{

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "computer_proposal_seq")
    public Long id;
    
    @Constraints.Required
    public String name;
    
    @Formats.DateTime(pattern="yyyy-MM-dd")
    public Date introduced;
    
    @Formats.DateTime(pattern="yyyy-MM-dd")
    public Date discontinued;
    
    
    /**
     * Return a page of computer
     *
     * @param page Page to display
     * @param pageSize Number of computers per page
     * @param sortBy Computer property used for sorting
     * @param order Sort order (either or asc or desc)
     * @param filter Filter applied on the name column
     */
    public static Page page(int page, int pageSize, String sortBy, String order, String filter, EntityManager em) {
        if(page < 1) page = 1;
        Long total = (Long)em
            .createQuery("select count(c) from ComputerProposal c where lower(c.name) like ?")
            .setParameter(1, "%" + filter.toLowerCase() + "%")
            .getSingleResult();
        @SuppressWarnings("unchecked")
				List<ComputerProposal> data = em
            .createQuery("from ComputerProposal c where lower(c.name) like ? order by c." + sortBy + " " + order)
            .setParameter(1, "%" + filter.toLowerCase() + "%")
            .setFirstResult((page - 1) * pageSize)
            .setMaxResults(pageSize)
            .getResultList();
        return new Page(data, total, page, pageSize);
    }
    
    /**
     * Used to represent a computers page.
     */
    public static class Page {
        
        private final int pageSize;
        private final long totalRowCount;
        private final int pageIndex;
        private final List<ComputerProposal> list;
        
        public Page(List<ComputerProposal> data, long total, int page, int pageSize) {
            this.list = data;
            this.totalRowCount = total;
            this.pageIndex = page;
            this.pageSize = pageSize;
        }
        
        public long getTotalRowCount() {
            return totalRowCount;
        }
        
        public int getPageIndex() {
            return pageIndex;
        }
        
        public List<ComputerProposal> getList() {
            return list;
        }
        
        public boolean hasPrev() {
            return pageIndex > 1;
        }
        
        public boolean hasNext() {
            return (totalRowCount/pageSize) >= pageIndex;
        }
        
        public String getDisplayXtoYofZ() {
            int start = ((pageIndex - 1) * pageSize + 1);
            int end = start + Math.min(pageSize, list.size()) - 1;
            return start + " to " + end + " of " + totalRowCount;
        }
        
    }
    
    
}
