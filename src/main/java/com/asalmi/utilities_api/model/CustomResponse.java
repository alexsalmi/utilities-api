package com.asalmi.utilities_api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Abstract Custom Response class, to be returned as REST responses
 */
@Getter
@Setter
@NoArgsConstructor
public abstract class CustomResponse {

  private Integer code;

  private String message;

  private Object details;

  abstract public boolean hasError();
}
