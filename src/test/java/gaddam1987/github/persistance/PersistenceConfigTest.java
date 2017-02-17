package gaddam1987.github.persistance;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PersistenceConfigTest.TestConfiguration.class)
public class PersistenceConfigTest {

    @Test
    public void testHello() {

    }

    @Configuration
    @Import({PersistenceConfig.class})
    static class TestConfiguration {
        @Bean
        public PlayingService playingService() {
            return mock(PlayingService.class);
        }
    }

}