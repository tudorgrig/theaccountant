package com.TheAccountant.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class MockFilterChain implements FilterChain {
    
    boolean shouldThrowExceptionOnDoFilter = false;
    
    public MockFilterChain(boolean shouldThrowExceptionOnDoFilter) {
    
        this.shouldThrowExceptionOnDoFilter = shouldThrowExceptionOnDoFilter;
    }
    
    /**
     * In case of this method is called, it means that the filter it allows the HTTP request 
     * to pass over the filter.
     */
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
    
        if (shouldThrowExceptionOnDoFilter) {
            throw new ServletException("MockFilterChain .doFilter() method should not be called!");
        }
        
        // set successful status
        ((HttpServletResponse) response).setStatus(200);
        
    }
    
}
