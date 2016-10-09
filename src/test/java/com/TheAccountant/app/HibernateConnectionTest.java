package com.TheAccountant.app;

import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
@Transactional
public class HibernateConnectionTest {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    public void shouldConnectToDatabase() {

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Session session = (Session) entityManager.getDelegate();
        assertTrue(session.isConnected());
    }
}