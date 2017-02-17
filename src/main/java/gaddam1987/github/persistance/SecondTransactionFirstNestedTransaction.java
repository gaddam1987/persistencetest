package gaddam1987.github.persistance;

import gaddam1987.github.persistance.entity.Customer;
import gaddam1987.github.persistance.entity.CustomerDTO;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Naresh on 17/02/2017.
 */
@Component
public class SecondTransactionFirstNestedTransaction {

    private final SessionFactory sessionFactory;

    @Autowired
    public SecondTransactionFirstNestedTransaction(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CustomerDTO fetchCustomer(Long id) {
        Customer customer = (Customer) sessionFactory.getCurrentSession().load(Customer.class, id);
        return new CustomerDTO(customer.getId(), customer.getFirstName(), customer.getLastName());
    }
}
