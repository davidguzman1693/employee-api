package com.coding.challenge.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "retry-config")
public class RetryConfigData {
  private Long initialIntervalMs;
  private Long maxIntervalMs;
  private Double multiplier;
  private Integer maxAttempts;
  private Long sleepTimeMs;
}
