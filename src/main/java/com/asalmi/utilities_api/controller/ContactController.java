package com.asalmi.utilities_api.controller;

import java.util.List;

import com.asalmi.utilities_api.exception.UtilitiesApiException;
import com.asalmi.utilities_api.model.ContactRequest;
import com.asalmi.utilities_api.model.ContactRequestBody;
import com.asalmi.utilities_api.model.CustomResponse;
import com.asalmi.utilities_api.model.ErrorResponse;
import com.asalmi.utilities_api.service.IContactService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for Contact Request operations
 */
@RestController
@CrossOrigin("${cors.whitelist.contactcontroller}")
@RequestMapping("/contact")
public class ContactController {

  @Autowired
  private IContactService contactService;

  /**
   * Send a contact request to Alex Salmi, and a confirmation email to the sender
   * 
   * @param {ContactRequestBody} The request body, including the sender's email,
   *                             name, message, and phone number (optional)
   * @return Custom Response with success or failure message
   */
  @PostMapping("/send")
  public ResponseEntity<CustomResponse> send(@Valid @RequestBody ContactRequestBody request)
      throws UtilitiesApiException {

    // Call the ContactService to send the contact request
    CustomResponse result = contactService.send(request);

    // If an error occurred, send an error response
    if (result.hasError()) {
      ErrorResponse errorResponse = (ErrorResponse) result;
      return ResponseEntity.status(HttpStatus.valueOf(errorResponse.getHttpStatus())).body(result);
    }

    // If success, send success response
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  /**
   * Get contact requests from the database
   * 
   * @param {String} (Optional) Email address
   * @return List of contact request entries
   */
  @GetMapping("/get")
  public ResponseEntity<List<ContactRequest>> get(@RequestParam(required = false) String email)
      throws UtilitiesApiException {

    // Call the ContactService to get the contact requests
    List<ContactRequest> result = contactService.get(email);

    // Send the fetched contact requests in the response
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  /**
   * Get a specific contact request from the database
   * 
   * @param {Long} Contact request ID
   * @return Contact request entry
   */
  @GetMapping("/get/{id}")
  public ResponseEntity<ContactRequest> getById(@PathVariable("id") Long id)
      throws UtilitiesApiException {

    // Call the ContactService to get the contact request
    ContactRequest result = contactService.getById(id);

    // Send the fetched contact request in the response
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }
}
