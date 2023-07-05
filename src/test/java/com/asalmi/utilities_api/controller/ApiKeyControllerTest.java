package com.asalmi.utilities_api.controller;

import static org.mockito.ArgumentMatchers.any;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.asalmi.utilities_api.constants.Constants;
import com.asalmi.utilities_api.exception.UtilitiesApiException;
import com.asalmi.utilities_api.model.ApiKeyRequest;
import com.asalmi.utilities_api.model.ApiKeyResponse;
import com.asalmi.utilities_api.model.ErrorResponse;
import com.asalmi.utilities_api.model.SuccessResponse;
import com.asalmi.utilities_api.service.IApiKeyService;
import com.asalmi.utilities_api.utils.Utils;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class ApiKeyControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private IApiKeyService apiKeyService;

  private ApiKeyRequest apiKeyRequest;
  private ApiKeyResponse apiKeyResponse;
  private ApiKeyResponse apiKeyValidateResponse;
  private String apiKey;
  private String application;
  private String consumer;

  @Before
  public void Setup() {
    apiKey = "API Key";
    application = "Utilites API";
    consumer = "Tests";

    apiKeyRequest = ApiKeyRequest.builder()
        .application(application)
        .consumer(consumer)
        .build();

    apiKeyResponse = ApiKeyResponse.builder()
        .apiKey(apiKey)
        .build();

    apiKeyValidateResponse = ApiKeyResponse.builder()
        .application(application)
        .consumer(consumer)
        .build();
  }

  @Test
  public void generate_Success() throws Exception {
    String serializedRequest = Utils.serializeObject(apiKeyRequest);

    SuccessResponse response = new SuccessResponse(apiKeyResponse);

    Mockito.when(apiKeyService.generate(any(String.class), any(String.class))).thenReturn(response);

    performGenerate(serializedRequest)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(Constants.SUCCESS_CODE))
        .andExpect(jsonPath("$.message").value(Constants.SUCCESS_MESSAGE))
        .andExpect(jsonPath("$.details.apiKey").value(apiKey));
  }

  @Test
  public void generate_BadRequest_MissingValues() throws Exception {
    ApiKeyRequest request = new ApiKeyRequest();
    String serializedRequest = Utils.serializeObject(request);

    ErrorResponse expectedResponse = new ErrorResponse(Constants.ERROR_CODE_BAD_REQUEST);
    List<String> errors = Arrays.asList("'consumer' field is required", "'application' field is required");

    performGenerate(serializedRequest)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(expectedResponse.getCode()))
        .andExpect(jsonPath("$.message").value(expectedResponse.getMessage()))
        .andExpect(jsonPath("$.details").isArray())
        .andExpect(jsonPath("$.details", hasSize(2)))
        .andExpect(jsonPath("$.details", hasItem(errors.get(0))))
        .andExpect(jsonPath("$.details", hasItem(errors.get(1))));
  }

  @Test
  public void generate_ServiceError() throws Exception {
    String serializedRequest = Utils.serializeObject(apiKeyRequest);

    ErrorResponse response = new ErrorResponse(Constants.ERROR_CODE_DEFAULT, "Test details");

    Mockito.when(apiKeyService.generate(any(String.class), any(String.class))).thenReturn(response);

    performGenerate(serializedRequest)
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code").value(response.getCode()))
        .andExpect(jsonPath("$.message").value(response.getMessage()))
        .andExpect(jsonPath("$.details").value(response.getDetails()));
  }

  @Test
  public void generate_ServiceException() throws Exception {
    String serializedRequest = Utils.serializeObject(apiKeyRequest);

    UtilitiesApiException exception = new UtilitiesApiException(Constants.ERROR_CODE_DEFAULT, "Test details");
    ErrorResponse response = new ErrorResponse(Constants.ERROR_CODE_DEFAULT, "Test details");

    Mockito.when(apiKeyService.generate(any(String.class), any(String.class))).thenThrow(exception);

    performGenerate(serializedRequest)
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code").value(response.getCode()))
        .andExpect(jsonPath("$.message").value(response.getMessage()))
        .andExpect(jsonPath("$.details").value(response.getDetails()));
  }

  @Test
  public void validate_Success() throws Exception {
    SuccessResponse response = new SuccessResponse(apiKeyValidateResponse);

    Mockito.when(apiKeyService.validate(any(String.class))).thenReturn(response);

    performValidate(apiKey)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(Constants.SUCCESS_CODE))
        .andExpect(jsonPath("$.message").value(Constants.SUCCESS_MESSAGE))
        .andExpect(jsonPath("$.details.application").value(apiKeyValidateResponse.getApplication()))
        .andExpect(jsonPath("$.details.consumer").value(apiKeyValidateResponse.getConsumer()));
  }

  @Test
  public void validate_ServiceError() throws Exception {
    ErrorResponse response = new ErrorResponse(Constants.ERROR_CODE_DEFAULT, Constants.ERROR_MESSAGE_API_KEY_NOT_FOUND);

    Mockito.when(apiKeyService.validate(any(String.class))).thenReturn(response);

    performValidate(apiKey)
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code").value(response.getCode()))
        .andExpect(jsonPath("$.message").value(response.getMessage()))
        .andExpect(jsonPath("$.details").value(response.getDetails()));
  }

  @Test
  public void validate_ServiceException() throws Exception {
    UtilitiesApiException exception = new UtilitiesApiException(Constants.ERROR_CODE_DEFAULT,
        Constants.ERROR_MESSAGE_HASH_API_KEY_FAILED);
    ErrorResponse response = new ErrorResponse(Constants.ERROR_CODE_DEFAULT,
        Constants.ERROR_MESSAGE_HASH_API_KEY_FAILED);

    Mockito.when(apiKeyService.validate(any(String.class))).thenThrow(exception);

    performValidate(apiKey)
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code").value(response.getCode()))
        .andExpect(jsonPath("$.message").value(response.getMessage()))
        .andExpect(jsonPath("$.details").value(response.getDetails()));
  }

  private ResultActions performGenerate(String request) throws Exception {
    MockHttpServletRequestBuilder postRequest = MockMvcRequestBuilders.post("/apikey/generate")
        .content(request)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .with(SecurityMockMvcRequestPostProcessors.user("test"));

    return mockMvc.perform(postRequest);
  }

  private ResultActions performValidate(String apiKey) throws Exception {
    MockHttpServletRequestBuilder getRequest = MockMvcRequestBuilders.get("/apikey/validate/" + apiKey)
        .with(SecurityMockMvcRequestPostProcessors.user("test"));

    return mockMvc.perform(getRequest);
  }
}
