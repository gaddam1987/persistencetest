package gaddam1987.github.persistance;

import gaddam1987.github.persistance.entity.Customer;
import gaddam1987.github.persistance.entity.CustomerDTO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Component
public class SecondTransaction {
    final SessionFactory sessionFactory;
    private final SecondTransactionFirstNestedTransaction firstNestedTransaction;
    private final SecondTransactionSecondNestedTransaction secondNestedTransaction;


    @Autowired
    public SecondTransaction(SessionFactory sessionFactory, SecondTransactionFirstNestedTransaction firstNestedTransaction, SecondTransactionFirstNestedTransaction firstNestedTransaction1, SecondTransactionSecondNestedTransaction secondNestedTransaction) {
        this.sessionFactory = sessionFactory;
        this.firstNestedTransaction = firstNestedTransaction1;
        this.secondNestedTransaction = secondNestedTransaction;
    }

    @Transactional(propagation = REQUIRES_NEW)
    public void doSecond(Long id) {
        final Session currentSession = sessionFactory.getCurrentSession();
        //getting customer
        CustomerDTO customerDTO = firstNestedTransaction.fetchCustomer(id);

        //some updates
        updateCustomer(customerDTO);

        //creating orer that depends on customer
        secondNestedTransaction.createOrder(id);
    }

    private void updateCustomer(CustomerDTO customerDTO){
        final Session currentSession = sessionFactory.getCurrentSession();
        Customer customer = new Customer(customerDTO.getFirstName(), "changed");
        customer.setId(customerDTO.getId());
        currentSession.update(customer);
    }
}
