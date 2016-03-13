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
        
        final String authorization = httpRequest.getHeader("Authorization");
        String loginUsername = sessionService.extractUsernameAndPassword(authorization).length == 0 ?
                "" : sessionService.extractUsernameAndPassword(authorization)[0];
        
        if (httpRequest.getMethod().equals("OPTIONS")) {
            return;
        }
        if (isAllowedURL(httpRequest.getRequestURI())) {
            chain.doFilter(request, response);
        } else if ((authorization != null && sessionService.isAValidAuthenticationString(authorization))) {
            SecurityContextHolder.getContext().setAuthentication(
                    new SessionAuthentication(loginUsername));
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
            if (url.contains("/user/login") 
                    || url.contains("/user/registration/") 
                    || url.contains("/user/add")
                    || url.contains("/descriprion")) {
                isAllowed = true;
            }
        }
        return isAllowed;
    }
    
    public void init(FilterConfig filterConfig) {
    
    }
    
    public void destroy() {
    
    }
    
}
