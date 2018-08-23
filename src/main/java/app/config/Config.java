package app.config;


import app.pagesLogic.Answer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class Config extends WebMvcConfigurerAdapter {

    @Bean
    SimpleDriverDataSource getH2DataSource(){
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(org.h2.Driver.class);
        dataSource.setUsername("internship");
        dataSource.setUrl("jdbc:h2:mem:test");
        dataSource.setPassword("internship");
        return dataSource;
    }

    @Bean
    SimpleDriverDataSource getOracleDataSource(){
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(oracle.jdbc.driver.OracleDriver.class);
        dataSource.setUsername("internship");
        dataSource.setUrl("jdbc:oracle:thin:@192.168.1.151:1521:gmudb");
        dataSource.setPassword("internship");
        return dataSource;
    }
}