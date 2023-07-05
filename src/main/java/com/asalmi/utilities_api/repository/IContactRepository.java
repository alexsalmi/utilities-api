package com.asalmi.utilities_api.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.asalmi.utilities_api.model.ContactRequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository class for Contact Request database operations
 */
@Repository
public interface IContactRepository extends JpaRepository<ContactRequest, Long> {
  // Get a specific contact request
  ContactRequest findOneById(Long id);

  // Get a list of contact requests specific to an email address
  List<ContactRequest> findByEmail(String email);

  // Get a list of contact requests between two dates
  List<ContactRequest> findByCreatedAtBetween(LocalDateTime createdAtStart, LocalDateTime createdAtEnd);
}
