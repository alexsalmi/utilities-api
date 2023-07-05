package com.asalmi.utilities_api.authentication;

import com.asalmi.utilities_api.constants.Constants;
import com.asalmi.utilities_api.utils.Utils;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.TimeUnit;

/**
 * Handles authenticating API keys against the database.
 */
public class ApiKeyAuthManager implements AuthenticationManager {
  private static final Logger log = LoggerFactory.getLogger(ApiKeyAuthManager.class);

  private final LoadingCache<String, Boolean> keys;

  /**
   * Constructor - loads API keys from database
   */
  public ApiKeyAuthManager(DataSource dataSource) {
    this.keys = Caffeine.newBuilder()
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .build(new DatabaseCacheLoader(dataSource));
  }

  /**
   * Compares provided API keys agains API keys in database
   */
  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String principal = (String) authentication.getPrincipal();

    if (!keys.get(principal)) {
      throw new BadCredentialsException("The API key was not found or not the expected value.");
    } else {
      authentication.setAuthenticated(true);
      return authentication;
    }
  }

  /**
   * Caffeine CacheLoader that checks the database for the api key if it not found
   * in the cache.
   */
  private static class DatabaseCacheLoader implements CacheLoader<String, Boolean> {
    private final DataSource dataSource;

    DatabaseCacheLoader(DataSource dataSource) {
      this.dataSource = dataSource;
    }

    @Override
    public Boolean load(String key) throws Exception {
      try (Connection conn = dataSource.getConnection()) {
        try (PreparedStatement ps = conn
            .prepareStatement("SELECT * FROM api_keys WHERE api_key_hash = ? AND application = ?")) {
          ps.setObject(1, new Utils().hashString(key));
          ps.setObject(2, Constants.APP_NAME);

          try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
              // Valid API Key
              return true;
            } else {
              // Invalid API Key
              return false;
            }
          }
        }
      } catch (Exception e) {
        log.error("An error occurred while retrieving api key from database", e);
        return false;
      }
    }
  }
}