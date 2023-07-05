package com.asalmi.utilities_api.authentication;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.asalmi.utilities_api.constants.Constants;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  /**
   * Customizes error message returned when an invalid API key is provided
   */
  @Override
  public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException authException)
      throws IOException, ServletException {
    res.setContentType("application/json;charset=UTF-8");
    res.setStatus(403);
    PrintWriter writer = res.getWriter();
    writer.print(Constants.ERROR_MESSAGE_INVALID_API_KEY);
    writer.flush();
    writer.close();
  }
}