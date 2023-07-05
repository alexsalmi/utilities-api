package com.asalmi.utilities_api.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Contact Request request body
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactRequestBody {

  @NotNull(message = "'email' field is required")
  @Email(message = "'email' field is not a valid email")
  private String email;

  @NotNull(message = "'message' field is required")
  private String message;

  @NotNull(message = "'name' field is required")
  private String name;

  private String phone;
}
