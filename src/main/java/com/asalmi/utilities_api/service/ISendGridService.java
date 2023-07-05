package com.asalmi.utilities_api.service;

import java.util.List;

import com.asalmi.utilities_api.exception.UtilitiesApiException;
import com.asalmi.utilities_api.model.ContactRequestBody;
import com.asalmi.utilities_api.model.CustomResponse;

/**
 * Interface for SendGrid Service class
 */
public interface ISendGridService {
  // Send email to user's email notifying them that Alex got their message
  CustomResponse sendNotificationEmail(ContactRequestBody request) throws UtilitiesApiException;

  // Send contact request email to Alex Salmi's email
  CustomResponse sendContactRequest(ContactRequestBody request) throws UtilitiesApiException;

  // Send email to Alex Salmi's email with a weekly summary of emails being sent
  // in the past month
  CustomResponse sendSummaryEmail(List<ContactRequestBody> monthlyRequests, long totalRequests)
      throws UtilitiesApiException;
}
