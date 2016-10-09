package com.TheAccountant.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Tudor
 * Test class for email validator util class
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
@Transactional
public class EmailValidatorTest {

    @Autowired
    private EmailValidator emailValidator;

    private static final String VALID_EMAIL = "my-money-tracker@gmail.com";
    private static final String INVALID_EMAIL_NO_USER = "@gmail.com";
    private static final String INVALID_EMAIL_NO_DOMAIN = "my-money-tracker@";
    private static final String INVALID_EMAIL_TOO_SHORT_DOMAIN = "my-money-tracker@gmail.c";

    @Test
    public void shouldValidateEmail() {

        assertTrue(emailValidator.validate(VALID_EMAIL));
    }

    @Test
    public void shouldRejectAsEmailDueToNoUser() {

        assertFalse(emailValidator.validate(INVALID_EMAIL_NO_USER));
    }

    @Test
    public void shouldRejectAsEmailDueToNoDomain() {

        assertFalse(emailValidator.validate(INVALID_EMAIL_NO_DOMAIN));
    }

    @Test
    public void shouldRejectAsEmailDueToTooShortDomain() {

        assertFalse(emailValidator.validate(INVALID_EMAIL_TOO_SHORT_DOMAIN));
    }
}
