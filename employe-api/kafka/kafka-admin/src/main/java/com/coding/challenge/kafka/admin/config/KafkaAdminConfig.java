package com.coding.challenge.kafka.admin.config;

import com.coding.challenge.config.KafkaConfigData;
import com.coding.challenge.config.RetryConfigData;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@EnableRetry
@Configuration
public class KafkaAdminConfig {

  private final RetryConfigData retryConfigData;
  private final KafkaConfigData kafkaConfigData;

  public KafkaAdminConfig(RetryConfigData retryConfigData, KafkaConfigData kafkaConfigData) {
    this.retryConfigData = retryConfigData;
    this.kafkaConfigData = kafkaConfigData;
  }

  @Bean
  public AdminClient adminClient() {
    return AdminClient.create(Map
        .of(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers()));
  }

  @Bean
  public WebClient webClient() {
    return WebClient.builder().build();
  }

  @Bean
  public RetryTemplate retryTemplate() {
    RetryTemplate retryTemplate = new RetryTemplate();

    //Increase wait time for each retry.
    ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
    exponentialBackOffPolicy.setInitialInterval(retryConfigData.getInitialIntervalMs());
    exponentialBackOffPolicy.setMaxInterval(retryConfigData.getMaxIntervalMs());
    exponentialBackOffPolicy.setMultiplier(retryConfigData.getMultiplier());
    retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);

    //Retry until max attemped is reached.
    SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
    simpleRetryPolicy.setMaxAttempts(retryConfigData.getMaxAttempts());
    retryTemplate.setRetryPolicy(simpleRetryPolicy);

    return retryTemplate;
  }
}
