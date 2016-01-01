package com.myMoneyTracker.util;

import javax.mail.MessagingException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test class for EmailSender
 * 
 * @author Florin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
@Transactional
public class EmailSenderTest {
    
    @Autowired
    EmailSender emailSender;
    
    @Test
    public void shouldSendEmail() {
    
        try {
            emailSender.sendEmail("rampageflo@gmail.com", "Test Subject", "Test Messagge");
        } catch (MessagingException e) {
            Assert.fail("Error on sending email: " + e.getMessage());
        }
    }
    
    @Test(expected = javax.mail.MessagingException.class)
    public void shouldNotSendEmail() throws MessagingException {
    
        emailSender.sendEmail("user@test_host.test", "Test Subject", "Test Messagge");
    }
}
