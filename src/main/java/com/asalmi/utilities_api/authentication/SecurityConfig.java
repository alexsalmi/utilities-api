package com.asalmi.utilities_api.authentication;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Autowired
  private DataSource dataSource;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    AuthenticationFilter filter = new AuthenticationFilter();
    filter.setAuthenticationManager(new ApiKeyAuthManager(dataSource));

    http.csrf(csrf -> csrf.disable())
        .cors(cors -> cors.getClass())
        .authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
        .sessionManagement(
            sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilter(filter)
        .exceptionHandling(handling -> handling.authenticationEntryPoint(new CustomAuthenticationEntryPoint()));

    return http.build();
  }

}