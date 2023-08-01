package com.coding.challenge.init;

import com.coding.challenge.config.KafkaConfigData;
import com.coding.challenge.kafka.admin.client.KafkaAdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KafkaStreamInitializer implements StreamInitializer {
  private static final Logger LOG = LoggerFactory.getLogger(KafkaStreamInitializer.class);

  private final KafkaConfigData kafkaConfigData;
  private final KafkaAdminClient kafkaAdminClient;

  public KafkaStreamInitializer(KafkaConfigData kafkaConfigData, KafkaAdminClient kafkaAdminClient) {
    this.kafkaConfigData = kafkaConfigData;
    this.kafkaAdminClient = kafkaAdminClient;
  }

  @Override
  public void init() {
    kafkaAdminClient.createTopics();
    kafkaAdminClient.checkSchemaRegistry();
    LOG.info("Topics with name {} are ready for operations", kafkaConfigData.getTopicNamesToCreate().toArray());
  }
}
