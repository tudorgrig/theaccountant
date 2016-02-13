package com.myMoneyTracker.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class MockFilterChain implements FilterChain {
    
    /**
     * In case of this method is called, it means that the filter it allows the HTTP request 
     * to pass over the filter.
     */
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
    
        // set successful status
        ((HttpServletResponse) response).setStatus(200);
        
    }
    
}