package com.coding.challenge.kafka.admin.client;

import com.coding.challenge.config.KafkaConfigData;
import com.coding.challenge.config.RetryConfigData;
import com.coding.challenge.kafka.admin.exception.KafkaClientException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


@Component
public class KafkaAdminClient {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaAdminClient.class);

  private final KafkaConfigData kafkaConfigData;
  private final RetryConfigData retryConfigData;
  private final AdminClient adminClient;

  private final RetryTemplate retryTemplate;

  private final WebClient webClient;

  public KafkaAdminClient(KafkaConfigData kafkaConfigData,
                          RetryConfigData retryConfigData,
                          AdminClient adminClient,
                          RetryTemplate retryTemplate,
                          WebClient webClient) {
    this.kafkaConfigData = kafkaConfigData;
    this.retryConfigData = retryConfigData;
    this.adminClient = adminClient;
    this.retryTemplate = retryTemplate;
    this.webClient = webClient;
  }

  public void createTopics() {
    CreateTopicsResult createTopicsResult;
    try {
      retryTemplate.execute(this::doCreateTopics); //Call a method with a retry logic.
    } catch (Exception e) {
      throw new KafkaClientException("Reached max number of retry for creating kafka topics!", e);
    }
    checkTopicsCreated();
  }

  private CreateTopicsResult doCreateTopics(RetryContext retryContext) {
    List<String> topicsNames = kafkaConfigData.getTopicNamesToCreate();
    LOG.info("Creating {} topic, attempt {}", topicsNames, retryContext.getRetryCount());
    List<NewTopic> kafkaTopics = topicsNames.stream().map(topic -> new NewTopic(
        topic.trim(),
        kafkaConfigData.getNumOfPartitions(),
        kafkaConfigData.getReplicationFactor()
    )).collect(Collectors.toList());

    return adminClient.createTopics(kafkaTopics);
  }

  public void checkTopicsCreated() {
    Collection<TopicListing> topics = getTopics();
    int retryCount = 1;
    Integer maxRetry = retryConfigData.getMaxAttempts();
    Integer multiplier = retryConfigData.getMultiplier().intValue();
    Long sleepTimeMs = retryConfigData.getSleepTimeMs();
    for (String topic : kafkaConfigData.getTopicNamesToCreate()) {
      //Custom retry logic to wait until topics created or max retry reached, increasing wait time exponentially.
      while (!isTopicCreated(topics, topic)) {
        checkMaxRetry(retryCount++, maxRetry);
        sleep(sleepTimeMs);
        sleepTimeMs *= multiplier;
        topics = getTopics();
      }
    }
  }

  private Collection<TopicListing> getTopics() {
    Collection<TopicListing> topics;
    try {
      topics = retryTemplate.execute(this::doGetTopics);
    } catch (Exception e) {
      throw new KafkaClientException("Reached max number of retry for getting kafka topics!", e);
    }
    return topics;
  }

  private Collection<TopicListing> doGetTopics(RetryContext retryContext)
      throws ExecutionException, InterruptedException {
    LOG.info("Reading kafka topic {}, attempt {}",
        kafkaConfigData.getTopicNamesToCreate().toArray(), retryContext.getRetryCount());
    Collection<TopicListing> topics = adminClient.listTopics().listings().get();
    if (topics != null) {
      topics.forEach(topic -> LOG.debug("Topic with name {}", topic.name()));
    }
    return topics;
  }

  public void checkSchemaRegistry() {
    int retryCount = 1;
    Integer maxRetry = retryConfigData.getMaxAttempts();
    Integer multiplier = retryConfigData.getMultiplier().intValue();
    Long sleepTimeMs = retryConfigData.getSleepTimeMs();
    while (getSchemaRegistryStatus().is2xxSuccessful()) {
      checkMaxRetry(retryCount++, maxRetry);
      sleep(sleepTimeMs);
      sleepTimeMs *= multiplier;
    }
  }

  //Using webflux for creating a web client.
  private HttpStatus getSchemaRegistryStatus() {
    try {
      HttpStatus httpStatus = webClient
          .method(HttpMethod.GET)
          .uri(kafkaConfigData.getSchemaRegistryUrlAlt())
          .exchange()
          .map(ClientResponse::statusCode)
          .block();

      return httpStatus;
    } catch (Exception e) {
      LOG.info("Error when comunicating with schema registry, ", e);
      return HttpStatus.SERVICE_UNAVAILABLE;
    }
  }

  private boolean isTopicCreated(Collection<TopicListing> topics, String topicName) {
    if (topics == null) {
      return false;
    }
    return topics.stream().anyMatch(topic -> topic.name().equals(topicName));
  }

  private void checkMaxRetry(int i, Integer maxRetry) {
    if (i > maxRetry) {
      throw new KafkaClientException("Reached max number of retry for reading kafka topics!");
    }
  }

  private void sleep(Long sleepTimeMs) {
    try {
      Thread.sleep(sleepTimeMs);
    } catch (InterruptedException e) {
      throw new KafkaClientException("Error while sleeping for waiting new created topics!", e);
    }
  }
}
