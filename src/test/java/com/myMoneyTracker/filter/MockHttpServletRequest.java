package com.myMoneyTracker.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

/**
 * Mock class used for tests.
 * This class implements {@link HttpServletRequest}
 * 
 * @author Florin
 */
public class MockHttpServletRequest implements HttpServletRequest {
    
    private String requestUri;
    private String method;
    private String clientIpAddress;
    private Map<String, String> headers = new HashMap<String, String>();
    
    public MockHttpServletRequest(String method, String requestUri, String clientIpAddress) {
    
        this.method = method;
        this.requestUri = requestUri;
        this.clientIpAddress = clientIpAddress;
    }
    
    public AsyncContext getAsyncContext() {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public Object getAttribute(String arg0) {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public Enumeration<String> getAttributeNames() {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getCharacterEncoding() {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public int getContentLength() {
    
        // TODO Auto-generated method stub
        return 0;
    }
    
    public long getContentLengthLong() {
    
        // TODO Auto-generated method stub
        return 0;
    }
    
    public String getContentType() {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public DispatcherType getDispatcherType() {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public ServletInputStream getInputStream() throws IOException {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getLocalAddr() {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getLocalName() {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public int getLocalPort() {
    
        // TODO Auto-generated method stub
        return 0;
    }
    
    public Locale getLocale() {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public Enumeration<Locale> getLocales() {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getParameter(String arg0) {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public Map<String, String[]> getParameterMap() {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public Enumeration<String> getParameterNames() {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public String[] getParameterValues(String arg0) {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getProtocol() {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public BufferedReader getReader() throws IOException {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getRealPath(String arg0) {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getRemoteAddr() {
    
        return this.clientIpAddress;
    }
    
    public String getRemoteHost() {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public int getRemotePort() {
    
        // TODO Auto-generated method stub
        return 0;
    }
    
    public RequestDispatcher getRequestDispatcher(String arg0) {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getScheme() {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getServerName() {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public int getServerPort() {
    
        // TODO Auto-generated method stub
        return 0;
    }
    
    public ServletContext getServletContext() {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public boolean isAsyncStarted() {
    
        // TODO Auto-generated method stub
        return false;
    }
    
    public boolean isAsyncSupported() {
    
        // TODO Auto-generated method stub
        return false;
    }
    
    public boolean isSecure() {
    
        // TODO Auto-generated method stub
        return false;
    }
    
    public void removeAttribute(String arg0) {
    
        // TODO Auto-generated method stub
        
    }
    
    public void setAttribute(String arg0, Object arg1) {
    
        // TODO Auto-generated method stub
        
    }
    
    public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {
    
        // TODO Auto-generated method stub
        
    }
    
    public AsyncContext startAsync() throws IllegalStateException {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1) throws IllegalStateException {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public boolean authenticate(HttpServletResponse arg0) throws IOException, ServletException {
    
        // TODO Auto-generated method stub
        return false;
    }
    
    public String changeSessionId() {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getAuthType() {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getContextPath() {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public Cookie[] getCookies() {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public long getDateHeader(String arg0) {
    
        // TODO Auto-generated method stub
        return 0;
    }
    
    public String getHeader(String headerName) {
    
        return headers.get(headerName);
    }
    
    public void putHeader(String headerName, String headerValue) {
    
        headers.put(headerName, headerValue);
    }
    
    public Enumeration<String> getHeaderNames() {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public Enumeration<String> getHeaders(String arg0) {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public int getIntHeader(String arg0) {
    
        // TODO Auto-generated method stub
        return 0;
    }
    
    public String getMethod() {
    
        return this.method;
    }
    
    public Part getPart(String arg0) throws IOException, ServletException {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public Collection<Part> getParts() throws IOException, ServletException {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getPathInfo() {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getPathTranslated() {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getQueryString() {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getRemoteUser() {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getRequestURI() {
    
        return this.requestUri;
    }
    
    public void setRequestURI(String requestUri) {
    
        this.requestUri = requestUri;
    }
    
    public StringBuffer getRequestURL() {
    
        return null;
    }
    
    public String getRequestedSessionId() {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getServletPath() {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public HttpSession getSession() {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public HttpSession getSession(boolean arg0) {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public Principal getUserPrincipal() {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public boolean isRequestedSessionIdFromCookie() {
    
        // TODO Auto-generated method stub
        return false;
    }
    
    public boolean isRequestedSessionIdFromURL() {
    
        // TODO Auto-generated method stub
        return false;
    }
    
    public boolean isRequestedSessionIdFromUrl() {
    
        // TODO Auto-generated method stub
        return false;
    }
    
    public boolean isRequestedSessionIdValid() {
    
        // TODO Auto-generated method stub
        return false;
    }
    
    public boolean isUserInRole(String arg0) {
    
        // TODO Auto-generated method stub
        return false;
    }
    
    public void login(String arg0, String arg1) throws ServletException {
    
        // TODO Auto-generated method stub
        
    }
    
    public void logout() throws ServletException {
    
        // TODO Auto-generated method stub
        
    }
    
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> arg0) throws IOException, ServletException {
    
        // TODO Auto-generated method stub
        return null;
    }
    
}
