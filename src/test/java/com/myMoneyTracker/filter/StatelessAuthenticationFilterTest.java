package com.myMoneyTracker.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.myMoneyTracker.app.filter.StatelessAuthenticationFilter;
import com.myMoneyTracker.app.service.SessionAuthentication;
import com.myMoneyTracker.model.user.AppUser;

/**
 * Test class for the {@link StatelessAuthenticationFilter} that is used to reject invalid requests.
 * 
 * @author Florin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
public class StatelessAuthenticationFilterTest {
    
    private StatelessAuthenticationFilter authFilter = new StatelessAuthenticationFilter();
    
    private String sessionToken;
    private AppUser appUser;
    
    @Before
    public void initializeContext() {
        appUser = createAppUser();
        sessionToken = "valid-session-token";
        SecurityContextHolder.getContext().setAuthentication(
                new SessionAuthentication(appUser, sessionToken));
    }
    
    @Test
    public void shouldAllowValidUrlRequests() {
    
        List<String> allowedURLs = createAllowedURLs();
        for (String url : allowedURLs) {
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();
            request.setRequestURI(url);
            try {
                authFilter.doFilter(request, response, new MockFilterChain());
            } catch (Exception e) {
                fail(e.getMessage());
            }
            assertEquals(200, response.getStatus());
        }
    }
    
    @Test
    public void shouldAllowValidSessionTokenRequest() {
    
        String url = "http://localhost:8080/expense/item1?name=name1";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.putHeader("mmtlt", sessionToken);
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI(url);
        try {
            authFilter.doFilter(request, response, new MockFilterChain());
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertEquals(200, response.getStatus());
    }
    
    @Test
    public void shouldNotAllowInvalidRequest() {
    
        String url = "http://localhost:8080/expense/item1?name=name1";
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI(url);
        try {
            authFilter.doFilter(request, response, new MockFilterChain());
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertEquals(401, response.getStatus());
    }
    
    private List<String> createAllowedURLs() {
    
        List<String> allowedUrls = new ArrayList<String>();
        allowedUrls.add("http://localhost:8080/login/item1?name=name1");
        allowedUrls.add("http://localhost:8080/registration/");
        allowedUrls.add("http://localhost:8080/user/add/2000");
        return allowedUrls;
    }
    
    private AppUser createAppUser() {

        AppUser appUser = new AppUser();
        appUser.setFirstName("Florin");
        appUser.setSurname("Iacob");
        appUser.setPassword("TEST_PASS");
        appUser.setUsername("florin");
        appUser.setBirthdate(new Date());
        appUser.setEmail("rampageflo@gmail.com");
        return appUser;
    }
}
