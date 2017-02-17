package gaddam1987.github.persistance;

import gaddam1987.github.persistance.entity.Customer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@Commit
public class ParentServiceTest extends PersistenceConfigTest {

    @Autowired
    private PlayingService playingService;

    @Autowired
    private ParentService parentService;


    @Test
    public void testInsert() {
//        doNothing().when(playingService).doSomeThingNeeded();
        parentService.createCustomer(new Customer("Naresh", "Reddy"));
    }

    @Test
    public void testWithException() {
        //doThrow(new RuntimeException("Hello")).when(playingService).doSomeThingNeeded();
        parentService.createCustomer(new Customer("Naresh", "kolo2"));
    }

}