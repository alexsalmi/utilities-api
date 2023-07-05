package com.asalmi.utilities_api.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;

import com.asalmi.utilities_api.constants.Constants;
import com.asalmi.utilities_api.exception.UtilitiesApiException;
import com.asalmi.utilities_api.model.ContactRequest;
import com.asalmi.utilities_api.model.ContactRequestBody;
import com.asalmi.utilities_api.model.CustomResponse;
import com.asalmi.utilities_api.model.SuccessResponse;
import com.asalmi.utilities_api.repository.IContactRepository;
import com.asalmi.utilities_api.service.IContactService;
import com.asalmi.utilities_api.service.ISendGridService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for Contact Request operations
 */
@Service
public class ContactService implements IContactService {
  @Autowired
  private IContactRepository contactRepository;

  @Autowired
  private ISendGridService sendGridService;

  private static final Logger log = LogManager.getLogger(ContactService.class);

  /**
   * Send a contact request to Alex Salmi, and a confirmation email to the sender
   * 
   * @param {ContactRequestBody} The request body, including the sender's email,
   *                             name, message, and phone number (optional)
   * @return Custom Response with success or failure message
   */
  public CustomResponse send(ContactRequestBody request) throws UtilitiesApiException {
    log.info("Started send contact request operation");

    // Send contact request email to Alex Salmi's email
    CustomResponse sgResponse = sendGridService.sendContactRequest(request);
    if (sgResponse.hasError()) {
      return sgResponse;
    }

    // Send email to user's email notifying them that Alex got their message
    sgResponse = sendGridService.sendNotificationEmail(request);
    if (sgResponse.hasError()) {
      return sgResponse;
    }

    // Build contact request entity to save to database
    ContactRequest message = ContactRequest.builder()
        .name(request.getName())
        .email(request.getEmail())
        .message(request.getMessage())
        .createdAt(LocalDateTime.now())
        .build();

    if (request.getPhone() != null) {
      message.setPhone(request.getPhone());
    }

    // Save contact request to database
    try {
      log.info("Saving contact request to database");
      contactRepository.save(message);
    } catch (Exception ex) {
      log.info("Failed to save contact request to database: " + ex.getMessage());
      throw new UtilitiesApiException(Constants.ERROR_CODE_DATABASE_SAVE_CR, ex.getMessage());
    }

    // If no errors occurred, return success response
    return new SuccessResponse(Constants.SUCCESS_DETAILS_SEND_CONTACT_REQUEST);
  }

  /**
   * Get contact requests from the database
   * 
   * @param {String} (Optional) Email address
   * @return List of contact request entries
   */
  public List<ContactRequest> get(String email) throws UtilitiesApiException {
    try {
      log.info("Fetching contact requests from database");

      // If no email was present in query, return all contact requests from database
      if (email == null) {
        return contactRepository.findAll();
      }

      // If email address was present, return all contact requests with that email
      log.info("Searching for contact requests with email address " + email);
      return contactRepository.findByEmail(email);
    } catch (Exception ex) {
      log.error("Failed to fetch contact requests from database: " + ex.getMessage());
      throw new UtilitiesApiException(Constants.ERROR_CODE_DATABASE_FETCH_CR, ex.getMessage());
    }
  }

  /**
   * Get a specific contact request from the database
   * 
   * @param {Long} Contact request ID
   * @return Contact request entry
   */
  public ContactRequest getById(Long id) throws UtilitiesApiException {
    try {
      log.info("Fetching contact request with id " + id + " from database");

      // Return contact request with specified id from database
      return contactRepository.findOneById(id);
    } catch (Exception ex) {
      log.error("Failed to fetch contact request with id " + id + " from database: " + ex.getMessage());
      throw new UtilitiesApiException(Constants.ERROR_CODE_DATABASE_FETCH_CR, ex.getMessage());
    }
  }
}
