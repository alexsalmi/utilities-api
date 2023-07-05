package com.asalmi.utilities_api.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * API Key request body
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiKeyRequest {
  @NotNull(message = "'consumer' field is required")
  private String consumer;

  @NotNull(message = "'application' field is required")
  private String application;
}
