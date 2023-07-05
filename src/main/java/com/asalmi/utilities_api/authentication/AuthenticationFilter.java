package com.asalmi.utilities_api.authentication;

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import com.asalmi.utilities_api.constants.Constants;

import jakarta.servlet.http.HttpServletRequest;

public class AuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

  @Override
  protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
    return request.getHeader(Constants.API_KEY_HEADER);
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    // No creds when using API key
    return null;
  }
}
