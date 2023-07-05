package com.asalmi.utilities_api.model;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.asalmi.utilities_api.constants.Constants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Error Response class to be returned as REST response for error cases
 */
@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse extends CustomResponse {

  private static final Logger log = LogManager.getLogger(ErrorResponse.class);

  @JsonProperty(access = Access.WRITE_ONLY)
  public Integer httpStatus;

  public ErrorResponse(Integer code) {
    getErrorInfo(code);
  }

  public ErrorResponse(Integer code, Object details) {
    getErrorInfo(code);
    this.setDetails(details);
  }

  public boolean hasError() {
    return true;
  }

  /**
   * Get error details from errors/errors.json file using the provided error code
   * 
   * @param {Integer} The error code to get the error info for
   */
  private void getErrorInfo(Integer code) {
    try {
      ObjectMapper mapper = new ObjectMapper();

      // Read file
      ClassPathResource classPathResource = new ClassPathResource(Constants.ERRORS_JSON_PATH);
      byte[] binaryData = FileCopyUtils.copyToByteArray(classPathResource.getInputStream());

      // Convert file contents to Map
      String jsonString = new String(binaryData, StandardCharsets.UTF_8);
      Map<Integer, ErrorResponse> errors = mapper.readValue(jsonString,
          new TypeReference<Map<Integer, ErrorResponse>>() {
          });

      // Get error info for the error code
      Integer errorCode = errors.containsKey(code) ? code : Constants.ERROR_CODE_DEFAULT;
      ErrorResponse temp = errors.get(errorCode);

      // Populate class fields
      this.setCode(temp.getCode());
      this.setMessage(temp.getMessage());
      this.setHttpStatus(temp.getHttpStatus());
    } catch (Exception ex) {
      log.error("Error creating ErrorResponse: " + ex.getMessage());
    }
  }
}
