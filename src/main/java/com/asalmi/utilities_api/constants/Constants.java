package com.asalmi.utilities_api.constants;

/**
 * Constants used throughout the Utilities API
 */
public class Constants {
  // Name of this application
  public static final String APP_NAME = "Utilities API";

  // Header name where the API Key must be passed
  public static final String API_KEY_HEADER = "x-api-key";

  // Success messaging
  public static final Integer SUCCESS_CODE = 2000;
  public static final String SUCCESS_MESSAGE = "Success";
  public static final String SUCCESS_DETAILS_SEND_CONTACT_REQUEST = "Contact request completed successfully";

  // Sendgrid related values
  public static final Integer SENDGRID_SEND_SUCCESS_CODE = 202;
  public static final String SENDGRID_ENDPOINT = "mail/send";
  public static final String SENDGRID_PERSONALIZATION_NAME = "name";
  public static final String SENDGRID_PERSONALIZATION_EMAIL = "email";
  public static final String SENDGRID_PERSONALIZATION_MESSAGE = "message";
  public static final String SENDGRID_PERSONALIZATION_PHONE = "phone";
  public static final String SENDGRID_PERSONALIZATION_MONTHLY_NUMBER = "monthly_number";
  public static final String SENDGRID_PERSONALIZATION_TOTAL_NUMBER = "total_number";
  public static final String SENDGRID_PERSONALIZATION_REQUESTS = "requests";
  public static final String SENDGRID_NOREPLY_EMAIL = "asalmi.noreply@gmail.com";
  public static final String SENDGRID_NOREPLY_NAME = "asalmi";
  public static final String SENDGRID_ASALMI_EMAIL = "alexjfsalmi@gmail.com";
  public static final String SENDGRID_ASALMI_NAME = "Alex Salmi";

  // Error codes
  public static final Integer ERROR_CODE_DEFAULT = 9999;
  public static final Integer ERROR_CODE_BAD_REQUEST = 4000;
  public static final Integer ERROR_CODE_NOT_FOUND = 4004;

  // 6xxx errors: Database related errors
  public static final Integer ERROR_CODE_DATABASE_SAVE_CR = 6000;
  public static final Integer ERROR_CODE_DATABASE_FETCH_CR = 6001;
  public static final Integer ERROR_CODE_DATABASE_SAVE_API_KEY = 6002;
  public static final Integer ERROR_CODE_DATABASE_FETCH_API_KEY = 6003;

  // 7xxx errors: External API errors
  public static final Integer ERROR_CODE_SENDGRID_SEND_ERROR = 7000;
  public static final Integer ERROR_CODE_SENDGRID_SEND_FAILED = 7001;

  // 8xxx errors: Internal logic errors
  public static final Integer ERROR_CODE_HASH_API_KEY = 8000;

  // Error messages
  public static final String ERROR_MESSAGE_INVALID_API_KEY = "Invalid API Key";
  public static final String ERROR_MESSAGE_API_KEY_NOT_FOUND = "API Key not found";
  public static final String ERROR_MESSAGE_HASH_API_KEY_FAILED = "Unable to validate API Key";

  public static final String ERRORS_JSON_PATH = "errors/errors.json";
}
