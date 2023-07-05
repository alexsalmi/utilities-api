package com.asalmi.utilities_api.controller;

import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.asalmi.utilities_api.constants.Constants;
import com.asalmi.utilities_api.exception.UtilitiesApiException;
import com.asalmi.utilities_api.model.ContactRequest;
import com.asalmi.utilities_api.model.ContactRequestBody;
import com.asalmi.utilities_api.model.ErrorResponse;
import com.asalmi.utilities_api.model.SuccessResponse;
import com.asalmi.utilities_api.service.IContactService;
import com.asalmi.utilities_api.utils.Utils;

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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class ContactControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private IContactService contactService;

  private ContactRequestBody contactRequestBody;
  private ContactRequest contactRequest;

  @Before
  public void Setup() {
    contactRequestBody = ContactRequestBody.builder()
        .email("test@test.com")
        .name("Test Name")
        .message("Test Message")
        .build();

    contactRequest = ContactRequest.builder()
        .id(Long.parseLong("1"))
        .email("test@test.com")
        .name("Test Name")
        .message("Test Message")
        .createdAt(LocalDateTime.now())
        .build();
  }

  @Test
  public void send_Success() throws Exception {
    String serializedRequest = Utils.serializeObject(contactRequestBody);

    SuccessResponse response = new SuccessResponse(Constants.SUCCESS_DETAILS_SEND_CONTACT_REQUEST);

    Mockito.when(contactService.send(any(ContactRequestBody.class))).thenReturn(response);

    performSend(serializedRequest)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(Constants.SUCCESS_CODE))
        .andExpect(jsonPath("$.message").value(Constants.SUCCESS_MESSAGE))
        .andExpect(jsonPath("$.details").value(Constants.SUCCESS_DETAILS_SEND_CONTACT_REQUEST));
  }

  @Test
  public void send_BadRequest_MissingValues() throws Exception {
    ContactRequestBody request = new ContactRequestBody();
    String serializedRequest = Utils.serializeObject(request);

    ErrorResponse expectedResponse = new ErrorResponse(Constants.ERROR_CODE_BAD_REQUEST);
    List<String> errors = Arrays.asList("'name' field is required", "'email' field is required",
        "'message' field is required");

    performSend(serializedRequest)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(expectedResponse.getCode()))
        .andExpect(jsonPath("$.message").value(expectedResponse.getMessage()))
        .andExpect(jsonPath("$.details").isArray())
        .andExpect(jsonPath("$.details", hasSize(3)))
        .andExpect(jsonPath("$.details", hasItem(errors.get(0))))
        .andExpect(jsonPath("$.details", hasItem(errors.get(1))))
        .andExpect(jsonPath("$.details", hasItem(errors.get(2))));
  }

  @Test
  public void send_BadRequest_InvalidEmail() throws Exception {
    ContactRequestBody request = contactRequestBody;
    request.setEmail("email");
    String serializedRequest = Utils.serializeObject(request);

    ErrorResponse expectedResponse = new ErrorResponse(Constants.ERROR_CODE_BAD_REQUEST);
    List<String> errors = Arrays.asList("'email' field is not a valid email");

    performSend(serializedRequest)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(expectedResponse.getCode()))
        .andExpect(jsonPath("$.message").value(expectedResponse.getMessage()))
        .andExpect(jsonPath("$.details").isArray())
        .andExpect(jsonPath("$.details", hasSize(1)))
        .andExpect(jsonPath("$.details", hasItem(errors.get(0))));
  }

  @Test
  public void send_ServiceError() throws Exception {
    String serializedRequest = Utils.serializeObject(contactRequestBody);

    ErrorResponse response = new ErrorResponse(Constants.ERROR_CODE_DEFAULT, "Test details");

    Mockito.when(contactService.send(any(ContactRequestBody.class))).thenReturn(response);

    performSend(serializedRequest)
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code").value(response.getCode()))
        .andExpect(jsonPath("$.message").value(response.getMessage()))
        .andExpect(jsonPath("$.details").value(response.getDetails()));
  }

  @Test
  public void send_ServiceException() throws Exception {
    String serializedRequest = Utils.serializeObject(contactRequestBody);

    UtilitiesApiException exception = new UtilitiesApiException(Constants.ERROR_CODE_DEFAULT, "Test details");
    ErrorResponse response = new ErrorResponse(Constants.ERROR_CODE_DEFAULT, "Test details");

    Mockito.when(contactService.send(any(ContactRequestBody.class))).thenThrow(exception);

    performSend(serializedRequest)
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code").value(response.getCode()))
        .andExpect(jsonPath("$.message").value(response.getMessage()))
        .andExpect(jsonPath("$.details").value(response.getDetails()));
  }

  @Test
  public void get_Success_NoEmail() throws Exception {
    List<ContactRequest> response = Arrays.asList(contactRequest);

    Mockito.when(contactService.get(null)).thenReturn(response);

    performGet(null)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id").value(response.get(0).getId()))
        .andExpect(jsonPath("$[0].email").value(response.get(0).getEmail()))
        .andExpect(jsonPath("$[0].name").value(response.get(0).getName()))
        .andExpect(jsonPath("$[0].message").value(response.get(0).getMessage()))
        .andExpect(jsonPath("$[0].phone").value(response.get(0).getPhone()));
  }

  @Test
  public void get_Success_WithEmail() throws Exception {
    List<ContactRequest> response = Arrays.asList(contactRequest);

    Mockito.when(contactService.get(any(String.class))).thenReturn(response);

    performGet("test@email.com")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id").value(response.get(0).getId()))
        .andExpect(jsonPath("$[0].email").value(response.get(0).getEmail()))
        .andExpect(jsonPath("$[0].name").value(response.get(0).getName()))
        .andExpect(jsonPath("$[0].message").value(response.get(0).getMessage()))
        .andExpect(jsonPath("$[0].phone").value(response.get(0).getPhone()));
  }

  @Test
  public void get_ServiceException() throws Exception {
    UtilitiesApiException exception = new UtilitiesApiException(Constants.ERROR_CODE_DEFAULT, "Test details");
    ErrorResponse response = new ErrorResponse(Constants.ERROR_CODE_DEFAULT, "Test details");

    Mockito.when(contactService.get(null)).thenThrow(exception);

    performGet(null)
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code").value(response.getCode()))
        .andExpect(jsonPath("$.message").value(response.getMessage()))
        .andExpect(jsonPath("$.details").value(response.getDetails()));
  }

  @Test
  public void getById_Success() throws Exception {
    ContactRequest response = contactRequest;

    Mockito.when(contactService.getById(any(Long.class))).thenReturn(response);

    performGetById(Long.parseLong("1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(response.getId()))
        .andExpect(jsonPath("$.email").value(response.getEmail()))
        .andExpect(jsonPath("$.name").value(response.getName()))
        .andExpect(jsonPath("$.message").value(response.getMessage()))
        .andExpect(jsonPath("$.phone").value(response.getPhone()));
  }

  @Test
  public void getById_ServiceException() throws Exception {
    UtilitiesApiException exception = new UtilitiesApiException(Constants.ERROR_CODE_DEFAULT, "Test details");
    ErrorResponse response = new ErrorResponse(Constants.ERROR_CODE_DEFAULT, "Test details");

    Mockito.when(contactService.getById(any(Long.class))).thenThrow(exception);

    performGetById(Long.parseLong("1"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code").value(response.getCode()))
        .andExpect(jsonPath("$.message").value(response.getMessage()))
        .andExpect(jsonPath("$.details").value(response.getDetails()));
  }

  private ResultActions performSend(String request) throws Exception {
    MockHttpServletRequestBuilder postRequest = MockMvcRequestBuilders.post("/contact/send")
        .content(request)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .with(SecurityMockMvcRequestPostProcessors.user("test"));

    return mockMvc.perform(postRequest);
  }

  private ResultActions performGet(String email) throws Exception {
    String path = "/contact/get";
    if (email != null) {
      path += "?email=" + email;
    }

    MockHttpServletRequestBuilder getRequest = MockMvcRequestBuilders.get(path)
        .with(SecurityMockMvcRequestPostProcessors.user("test"));
    return mockMvc.perform(getRequest);
  }

  private ResultActions performGetById(Long id) throws Exception {
    String path = "/contact/get/" + id;

    MockHttpServletRequestBuilder getRequest = MockMvcRequestBuilders.get(path)
        .with(SecurityMockMvcRequestPostProcessors.user("test"));
    return mockMvc.perform(getRequest);
  }
}