package com.TheAccountant.util;

import com.TheAccountant.controller.exception.BadRequestException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by tudor.grigoriu on 10/26/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
@TestPropertySource(locations="classpath:application-test.properties")
public class UserUtilTest {

    @Autowired
    private UserUtil userUtil;

    @Test(expected = BadRequestException.class)
    public void shouldThrowExceptionOnValidateUsername(){
        String username = "tudor/grigoriu";
        userUtil.validateUsername(username);
    }

}
