package gaddam1987.github.persistance;


import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.zaxxer.hikari.HikariDataSource;
import gaddam1987.github.persistance.entity.Customer;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.System.setProperty;
import static org.mockito.Mockito.mock;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.H2;

@Configuration
@ComponentScan("gaddam1987.github.persistance")
@EnableTransactionManagement
public class PersistenceConfig {
    private static final Logger logger = LoggerFactory.getLogger(PersistenceConfig.class);

    static {
        setProperty("org.jboss.logging.provider", "slf4j");
    }

//    @Bean
//    LocalContainerEntityManagerFactoryBean entityManagerFactory() {
//        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//        em.setDataSource(dataSource());
//        em.setPackagesToScan("gaddam1987.github.persistance.entity");
//        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        em.setJpaVendorAdapter(vendorAdapter);
//        em.setJpaProperties(additionalProperties());
//        return em;
//    }

    @Bean
    LocalSessionFactoryBean sessionFactoryBean() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("gaddam1987.github.persistance.entity");
        sessionFactory.setHibernateProperties(additionalProperties());
        return sessionFactory;
    }

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:sqlserver://vaapdb1.database.secure.windows.net:1433;databaseName=vaap1db");

        dataSource.setUsername("sa");
        dataSource.setPassword("Ganesh@123");
        dataSource.setDataSource(new EmbeddedDatabaseBuilder().setType(H2).build());
        dataSource.setMaximumPoolSize(30);
        dataSource.setConnectionTimeout(1000);
        dataSource.setLeakDetectionThreshold(3000);
        dataSource.setConnectionInitSql("select 1");
        dataSource.setHealthCheckRegistry(healthCheckRegistry());
        dataSource.setMetricRegistry(metricRegistry());
        dataSource.setRegisterMbeans(true);
        dataSource.addHealthCheckProperty("expected99thPercentileMs", "10");

        return ProxyDataSourceBuilder
                .create(dataSource)
                .logQueryBySlf4j()
                .countQuery()
                .build();
    }

    @Bean
    MetricRegistry metricRegistry() {
        return new MetricRegistry();
    }

    @Bean
    HealthCheckRegistry healthCheckRegistry() {
        return new HealthCheckRegistry();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setDataSource(dataSource());
        transactionManager.setSessionFactory(sessionFactoryBean().getObject());
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    private Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "create");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.format_sql", "true");
        properties.setProperty("hibernate.use_sql_comments", "true");
        return properties;
    }

    @Bean
    public PlayingService playingService() {
        return mock(PlayingService.class);
    }


    public static void main(String[] args) throws IOException {
        ApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(PersistenceConfig.class);

        ParentService bean = applicationContext.getBean(ParentService.class);
        MetricRegistry metricRegistry = applicationContext.getBean(MetricRegistry.class);
        HealthCheckRegistry healthCheckRegistry = applicationContext.getBean(HealthCheckRegistry.class);
        JmxReporter reporter = JmxReporter
                .forRegistry(metricRegistry)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();

        reporter.start();

        Executors.newFixedThreadPool(5).submit(() -> {
            bean.createCustomer(new Customer("Naresh", "Reddy"));

        });
        InputStream in = System.in;

        in.read();
    }
}
