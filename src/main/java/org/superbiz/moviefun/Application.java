package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class Application {


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ServletRegistrationBean actionServletRegistration(ActionServlet actionServlet) {
        return new ServletRegistrationBean(actionServlet, "/moviefun/*");
    }

    @Bean
    public DatabaseServiceCredentials databaseServiceCredentials(){

        return new DatabaseServiceCredentials(System.getProperty("VCAP_SERVICE"));

    }

    @Bean
    public HibernateJpaVendorAdapter hibernateJpaVendorAdapter(){
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        vendorAdapter.setDatabase(Database.MYSQL);
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        return vendorAdapter;
    }

    @Bean
    public DataSource albumsDataSource(DatabaseServiceCredentials serviceCredentials) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(serviceCredentials.jdbcUrl("albums-mysql"));
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean albumEMFBean(DataSource albumsDataSource, HibernateJpaVendorAdapter hibernateJpaVendorAdapter){

        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(albumsDataSource);
        localContainerEntityManagerFactoryBean.setPackagesToScan("org.superbiz.moviefun.albums");
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("albumPersistenceUnit");
        //em.setJpaProperties(additionalProperties());

        return localContainerEntityManagerFactoryBean;
    }

    @Bean(name = "albumPlatformTransactionManager")
    public PlatformTransactionManager albumPlatformTransactionManager(EntityManagerFactory albumEMFBean){
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(albumEMFBean);
        return  jpaTransactionManager;
    }

    @Bean
    public DataSource moviesDataSource(DatabaseServiceCredentials serviceCredentials) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(serviceCredentials.jdbcUrl("movies-mysql"));
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean movieEMFBean (DataSource moviesDataSource, HibernateJpaVendorAdapter hibernateJpaVendorAdapter){

        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(moviesDataSource);
        localContainerEntityManagerFactoryBean.setPackagesToScan("org.superbiz.moviefun.movies");
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("moviePersistenceUnit");
        //em.setJpaProperties(additionalProperties());

        return localContainerEntityManagerFactoryBean;
    }

    @Bean(name = "moviePlatformTransactionManager")
    public PlatformTransactionManager moviePlatformTransactionManager(EntityManagerFactory movieEMFBean){
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(movieEMFBean);
        return jpaTransactionManager;
    }













}
