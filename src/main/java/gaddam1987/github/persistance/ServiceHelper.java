package gaddam1987.github.persistance;

import gaddam1987.github.persistance.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Naresh on 09/02/2017.
 */
@Service
public class ServiceHelper extends BaseDAOHibernateImpl {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Customer methodToPersist(String firstName, String lastName) throws DatabaseException {
        Customer entity = new Customer(firstName, lastName);
        Customer customer = (Customer) saveOrUpdate(entity);
        return customer;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Customer methodToUpdate(Long id, String firstName, String lastName) throws DatabaseException {
        Customer entity = new Customer(firstName, lastName);
        entity.setId(id);
        return (Customer) saveOrUpdate(entity);
    }


}
