package com.asalmi.utilities_api.service;

import java.util.List;

import com.asalmi.utilities_api.exception.UtilitiesApiException;
import com.asalmi.utilities_api.model.ContactRequest;
import com.asalmi.utilities_api.model.ContactRequestBody;
import com.asalmi.utilities_api.model.CustomResponse;

/**
 * Interface for Contact Request Service class
 */
public interface IContactService {

  // Send a contact request to Alex Salmi, and a confirmation email to the sender
  CustomResponse send(ContactRequestBody request) throws UtilitiesApiException;

  // Get the contact requests from the DB
  List<ContactRequest> get(String email) throws UtilitiesApiException;

  // Get a specific contact reqeust from the DB
  ContactRequest getById(Long id) throws UtilitiesApiException;
}
