package gaddam1987.github.persistance;

import gaddam1987.github.persistance.entity.Customer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service
public class ParentService {
    @Autowired
    PlayingService playingService;

    @Autowired
    ServiceHelper serviceHelper;

    @Transactional(propagation = REQUIRES_NEW)
    public void createCustomer(Customer customer) throws DatabaseException {
        Customer cust = serviceHelper.methodToPersist(customer.getFirstName(), customer.getLastName());

        playingService.doSomeThingNeeded();

        serviceHelper.methodToUpdate(cust.getId(), "dinesh", "lal");
    }
}
