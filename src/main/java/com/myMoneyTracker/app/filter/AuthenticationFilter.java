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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.myMoneyTracker.app.authentication.SessionAuthentication;
import com.myMoneyTracker.service.SessionService;

@Component
class AuthenticationFilter implements Filter {
    
    @Autowired
    private SessionService sessionService;
    
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        if (httpRequest.getMethod().equals("OPTIONS")) {
            return;
        }
        
        final String authorization = httpRequest.getHeader("Authorization");
        String[] credentials = sessionService.extractUsernameAndPassword(authorization);
        String loginUsername = credentials.length == 0 ? null : credentials[0];
        String clientIpAddress = extractClientIpAddress(httpRequest);
        SecurityContextHolder.getContext().setAuthentication(new SessionAuthentication(loginUsername, clientIpAddress));
        
        if (isAllowedURL(httpRequest.getRequestURI())) {
            chain.doFilter(request, response);
        } else if ((authorization != null && sessionService.isAValidAuthenticationString(authorization, clientIpAddress))) {
            chain.doFilter(request, response);
        } else {
            httpResponse.setStatus(401);
        }
    }
    
    private boolean isAllowedURL(String url) {
    
        boolean isAllowed = false;
        if (url != null) {
            if (url.contains("?")) {
                url = url.substring(0, url.indexOf('?'));
            }
            if (url.contains("/user/login") || url.contains("/user/logout") || url.contains("/user/registration/") || url.contains("/user/add") || url.contains("/description")) {
                isAllowed = true;
            }
        }
        return isAllowed;
    }
    
    public void init(FilterConfig filterConfig) {
    
    }
    
    public void destroy() {
    
    }
    
    private String extractClientIpAddress(HttpServletRequest request) {
      //is client behind something?
        String ipAddress = request.getHeader("X-FORWARDED-FOR");  
        if (ipAddress == null) {  
            ipAddress = request.getRemoteAddr();  
        }
        return ipAddress;
    }
    
}
