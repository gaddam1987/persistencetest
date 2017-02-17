package gaddam1987.github.persistance;


import gaddam1987.github.persistance.entity.Customer;
import gaddam1987.github.persistance.entity.Order;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManagerFactory;

@Component
public class SecondTransactionSecondNestedTransaction {

    private final SessionFactory sessionFactory;

    @Autowired
    public SecondTransactionSecondNestedTransaction(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createOrder(Long id) {
        Session currentSession = sessionFactory.getCurrentSession();
        Customer customer = (Customer) currentSession.get(Customer.class, id);
        Order order = new Order();
        order.setCustomer(customer);
        currentSession.persist(order);
    }
}
