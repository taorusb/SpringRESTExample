package com.taorusb.springrestexample.config;

import com.taorusb.springrestexample.security.JwtConfigurer;
import com.taorusb.springrestexample.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/api/v1/login").permitAll()
                .antMatchers("/api/v1/admin/**").hasAuthority("ROLE_ADMIN")
                .antMatchers(HttpMethod.GET, "/api/v1/users/**", "/api/v1/users").hasAnyAuthority("ROLE_USER")
                .antMatchers(HttpMethod.POST, "/api/v1/user", "/api/v1/file").hasAnyAuthority("ROLE_MODERATOR")
                .antMatchers(HttpMethod.PUT, "/api/v1/user", "/api/v1/file").hasAnyAuthority("ROLE_MODERATOR")
                .antMatchers(HttpMethod.DELETE, "/api/v1/user/**", "/api/v1/file/**").hasAnyAuthority("ROLE_MODERATOR")
                .anyRequest().authenticated()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider));
    }
}