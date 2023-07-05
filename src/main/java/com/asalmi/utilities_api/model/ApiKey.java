package com.asalmi.utilities_api.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * API Key entity class
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "api_keys")
public class ApiKey {

  @Id
  @Column(name = "api_key_hash")
  private String apiKeyHash;

  @Column(name = "application", nullable = false)
  private String application;

  @Column(name = "consumer", nullable = false)
  private String consumer;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

}
