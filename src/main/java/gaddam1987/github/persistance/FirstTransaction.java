package gaddam1987.github.persistance;

import gaddam1987.github.persistance.entity.Customer;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Naresh on 09/02/2017.
 */
@Service
public class FirstTransaction {

    final SessionFactory sessionFactory;

    @Autowired
    public FirstTransaction(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Customer doFirst(String firstName, String lastName) {
        Customer entity = new Customer(firstName, lastName);
        this.sessionFactory.getCurrentSession().persist(entity);
        return entity;
    }
}
