package com.asalmi.utilities_api.utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * Service class containing shared utility funcitons
 */
@Service
public class Utils {

  /**
   * Converts a string of any length into a fixed size hash
   * 
   * @param {String} the string to be hashed
   * @return The hashed string
   */
  public String hashString(String input) throws NoSuchAlgorithmException {
    String hashtext = null;
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));

    hashtext = convertToHex(messageDigest);
    return hashtext;
  }

  /**
   * Helper function to convert a byte array to hex string
   * 
   * @param {byte[]} Byte array to be converted to hex
   * @return The converted string
   */
  private String convertToHex(final byte[] messageDigest) {
    BigInteger bigint = new BigInteger(1, messageDigest);
    String hexText = bigint.toString(16);
    while (hexText.length() < 32) {
      hexText = "0".concat(hexText);
    }
    return hexText;
  }

  /**
   * Converts an object into a serialized JSON string
   * 
   * @param {Object} The object to be converted into a JSON string
   * @return The converted JSON string
   */
  public static String serializeObject(Object request) throws JsonProcessingException {
    ObjectWriter writer = new ObjectMapper().writer();
    return writer.writeValueAsString(request);
  }
}
