package com.myMoneyTracker.app.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;

import com.myMoneyTracker.app.service.SessionAuthentication;

/**
 * Class implementing {@link Filter} that will handle unauthorized access.
 * 
 * @author Florin
 */
public class StatelessAuthenticationFilter implements Filter {
    
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        if (isAllowedURL(httpRequest.getRequestURI())) {
            chain.doFilter(request, response);
        } else {
            String headerToken = httpRequest.getHeader("mmtlt");
            SessionAuthentication sessionAuthentication = (SessionAuthentication) SecurityContextHolder.getContext().getAuthentication();
            String sessionToken = sessionAuthentication == null ? null : sessionAuthentication.getSessionToken();
            if (headerToken != null && headerToken.equals(sessionToken)) {
                chain.doFilter(request, response);
            } else {
                httpResponse.setStatus(401);
                response.getOutputStream().print("Unauthorized access!");
            }
        }
    }
    
    private boolean isAllowedURL(String url) {
    
        boolean isAllowed = false;
        if (url != null) {
            if (url.contains("?")) {
                url = url.substring(0, url.indexOf('?'));
            }
            if (url.contains("/login/") || url.contains("/registration/") || url.contains("/user/add")) {
                isAllowed = true;
            }
        }
        return isAllowed;
    }
    
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }
    
    public void destroy() {
    
    }
    
}
