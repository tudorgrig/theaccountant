package com.myMoneyTracker.app;

import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/spring-config.xml"})
@Transactional
public class HibernateConnectionTest {
    @Qualifier("sessionFactory")
    @Autowired
    private SessionFactory sessionFactory;

    @Test
    public void testSomething() throws SQLException {
        DataSource dataSource = SessionFactoryUtils.getDataSource(sessionFactory);
        Connection connection =  dataSource.getConnection();
        assertNotNull(connection);
    }
}