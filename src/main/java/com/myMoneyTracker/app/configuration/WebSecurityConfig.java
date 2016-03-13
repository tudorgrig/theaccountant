package com.myMoneyTracker.app.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import com.myMoneyTracker.app.filter.CsrfHeaderFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    
        // forces the requests on HTTPS
        http.requiresChannel().anyRequest().requiresSecure();
        http.csrf().disable(); 
        http.authorizeRequests().antMatchers("/*").permitAll().anyRequest().permitAll();

    }
}