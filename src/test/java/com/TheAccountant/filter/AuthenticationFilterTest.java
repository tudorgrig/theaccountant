package com.TheAccountant.filter;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.TheAccountant.app.filter.AuthenticationFilter;
import com.TheAccountant.dao.AuthenticatedSessionDao;
import com.TheAccountant.model.session.AuthenticatedSession;
import com.TheAccountant.service.SessionService;

/**
 * This class represents the test class for the {@link AuthenticationFilter} class, 
 * the filter that is responsible to filter unauthorized access.
 * 
 * @author Florin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
@TestPropertySource(locations="classpath:application-test.properties")
public class AuthenticationFilterTest {
    
    private static final String BASE_URL = "https://localhost:8443";
    private static final String LOGIN_PATH = "/user/login";
    private String clientIpAddress = "1.1.1.1";
    private String username = "my_username";
    private String password = "my_password";
    
    @Autowired
    private AuthenticationFilter authenticationFilter;
    
    @Autowired
    private SessionService sessionService;
    
    @Autowired
    private AuthenticatedSessionDao authenticatedSessionDao;
    
    @Test
    public void shouldNotCallDoFilterOnOptionsMethod() {
        
        MockFilterChain filterChain = new MockFilterChain(true);
        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", BASE_URL + LOGIN_PATH, clientIpAddress);
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        try {
            authenticationFilter.doFilter(request, response, filterChain);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        } catch (ServletException e) {
            Assert.fail(e.getMessage());
        }
    }
    
    @Test
    public void shouldPassAllowedURL() {
        
        MockFilterChain filterChain = new MockFilterChain(false);
        
        String requestUrl1 = BASE_URL + LOGIN_PATH;
        MockHttpServletRequest request1 = new MockHttpServletRequest("GET", requestUrl1, clientIpAddress);
        MockHttpServletResponse response1 = new MockHttpServletResponse();
        
        String requestUrl2 = BASE_URL + "/user/activation/aaaaaaaaaa";
        MockHttpServletRequest request2 = new MockHttpServletRequest("GET", requestUrl2, clientIpAddress);
        MockHttpServletResponse response2 = new MockHttpServletResponse();

        try {
            authenticationFilter.doFilter(request1, response1, filterChain);
            authenticationFilter.doFilter(request2, response2, filterChain);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        } catch (ServletException e) {
            Assert.fail(e.getMessage());
        }
        
        Assert.assertEquals(requestUrl1 + " should be allowed!", 200, response1.getStatus());
        Assert.assertEquals(requestUrl2 + " should be allowed!", 200, response2.getStatus());
    }
    
    @Test
    public void shouldNotPassUnauthorizedAttempts() {
        
        MockFilterChain filterChain = new MockFilterChain(false);
        
        String requestUrl1 = BASE_URL + "/expense/find?id=100";
        MockHttpServletRequest request1 = new MockHttpServletRequest("GET", requestUrl1, clientIpAddress);
        MockHttpServletResponse response1 = new MockHttpServletResponse();
        
        String requestUrl2 = BASE_URL + "/income/add";
        MockHttpServletRequest request2 = new MockHttpServletRequest("POST", requestUrl2, clientIpAddress);
        MockHttpServletResponse response2 = new MockHttpServletResponse();
        
        String requestUrl3 = BASE_URL + "/income/find?id=100";
        MockHttpServletRequest request3 = new MockHttpServletRequest("GET", requestUrl2, clientIpAddress);
        String authorizationString = sessionService.encodeUsernameAndPassword(username, password);
        request3.putHeader("Authorization", authorizationString);
        MockHttpServletResponse response3 = new MockHttpServletResponse();

        try {
            authenticationFilter.doFilter(request1, response1, filterChain);
            authenticationFilter.doFilter(request2, response2, filterChain);
            authenticationFilter.doFilter(request3, response3, filterChain);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        } catch (ServletException e) {
            Assert.fail(e.getMessage());
        }
        
        Assert.assertEquals(requestUrl1 + " should not be allowed!", 401, response1.getStatus());
        Assert.assertEquals(requestUrl2 + " should not be allowed!", 401, response2.getStatus());
        Assert.assertEquals(requestUrl3 + " should not be allowed!", 401, response3.getStatus());
    }
    
    @Test
    public void shouldPassAuthorizedAttempt() {
        
        MockFilterChain filterChain = new MockFilterChain(false);
        
        System.out.println(" >>>> Florinho -1 : shouldPassAuthorizedAttempt");
        
        String authorizationString = sessionService.encodeUsernameAndPassword(username, password);
        AuthenticatedSession authenticatedSession = new AuthenticatedSession(authorizationString, username, "1.1.1.1", 
                sessionService.calculateExpirationTimeStartingFromNow());
        boolean added = sessionService.addAuthenticatedSession(authenticatedSession);
        Assert.assertTrue("Authenticated Session should be added!", added);

        String requestUrl = BASE_URL + "/expense/find_all";
        MockHttpServletRequest request = new MockHttpServletRequest("GET", requestUrl, clientIpAddress);
        request.putHeader("Authorization", authorizationString);
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        try {
            authenticationFilter.doFilter(request, response, filterChain);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        } catch (ServletException e) {
            Assert.fail(e.getMessage());
        }
        
        Assert.assertEquals(requestUrl + " should be authorized!", 200, response.getStatus());
    }
}
