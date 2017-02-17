package gaddam1987.github.persistance;

import gaddam1987.github.persistance.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service
public class ParentService {

    @Autowired
    FirstTransaction serviceHelper;

    @Autowired
    SecondTransaction secondTransaction;


    @Transactional(propagation = REQUIRES_NEW)
    public void createCustomer(Customer customer) {
//        Long customerId = serviceHelper.createCustomer(customer.getFirstName(), customer.getLastName());
//
//        playingService.doSomeThingNeeded();
//
//        serviceHelper.methodToUpdate(cust.getId(), "dinesh", "lal");
        Customer customer1 = serviceHelper.doFirst("Naresh", "Reddy");
        secondTransaction.doSecond(customer1.getId());
    }
}
