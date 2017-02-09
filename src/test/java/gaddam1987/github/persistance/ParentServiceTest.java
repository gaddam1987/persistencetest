package gaddam1987.github.persistance;

import gaddam1987.github.persistance.entity.Customer;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@Commit
public class ParentServiceTest extends PersistenceConfigTest {

    @Autowired
    private PlayingService playingService;

    @Autowired
    private ParentService parentService;

    @Autowired
    SessionFactory sessionFactory;

    @Test
    public void testInsert() throws DatabaseException {
        doNothing().when(playingService).doSomeThingNeeded();
        parentService.createCustomer(new Customer("Naresh", "Reddy"));
    }

    @Test
    public void testWithException() throws DatabaseException {
        doThrow(new RuntimeException("Hello")).when(playingService).doSomeThingNeeded();
        parentService.createCustomer(new Customer("Naresh", "kolo2"));
    }

}