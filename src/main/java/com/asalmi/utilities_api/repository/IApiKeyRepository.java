package com.asalmi.utilities_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.asalmi.utilities_api.model.ApiKey;

/**
 * Repository class for API Key database operations
 */
@Repository
public interface IApiKeyRepository extends JpaRepository<ApiKey, Long> {

  // Get a specific api_key
  ApiKey findOneByApiKeyHash(String apiKeyHash);
}