package gaddam1987.github.persistance;


import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.zaxxer.hikari.HikariDataSource;
import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
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
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static org.mockito.Mockito.mock;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.H2;

@Configuration
@ComponentScan("gaddam1987.github.persistance")
@EnableTransactionManagement
//@EnableWebMvc
public class PersistenceConfig {

    static {
        System.setProperty("org.jboss.logging.provider", "slf4j");
    }

    private static final Logger logger = LoggerFactory.getLogger(PersistenceConfig.class);

    /**
     * @Bean LocalContainerEntityManagerFactoryBean entityManagerFactory() {
     * LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
     * em.setDataSource(dataSource());
     * em.setPackagesToScan("gaddam1987.github.persistance.entity");
     * <p>
     * JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
     * em.setJpaVendorAdapter(vendorAdapter);
     * em.setJpaProperties(additionalProperties());
     * return em;
     * }
     **/

    @Bean
    LocalSessionFactoryBean localSessionFactoryBean(DataSource dataSource) {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setPackagesToScan("gaddam1987.github.persistance.entity");
        sessionFactoryBean.setHibernateProperties(additionalProperties());
        return sessionFactoryBean;
    }

    @Bean
    public DataSource dataSource() {
        /**
         SQLServerDataSource sqlServerDataSource = new SQLServerDataSource();
         **/

        HikariDataSource dataSource = new HikariDataSource();
        /**
         dataSource.setJdbcUrl("");
         dataSource.setUsername("");
         dataSource.setPassword("");
         **/
        dataSource.setDataSource(new EmbeddedDatabaseBuilder().setType(H2).build());
        dataSource.setMaximumPoolSize(30);
        dataSource.setConnectionTimeout(1000);
        //dataSource.setLeakDetectionThreshold(3000);
        dataSource.setConnectionInitSql("select 1");

        return ProxyDataSourceBuilder
                .create(dataSource)
                .logQueryBySlf4j()
                .countQuery()
                .build();
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();

        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(localSessionFactoryBean(dataSource).getObject());
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    private Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "create");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLServer2012Dialect");
        //properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
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

        System.in.read();
    }
}
