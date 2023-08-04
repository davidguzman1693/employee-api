package com.coding.challenge.consumer;

import com.coding.challenge.config.KafkaConfigData;
import com.coding.challenge.config.KafkaConsumerConfigData;
import com.coding.challenge.kafka.admin.client.KafkaAdminClient;
import com.coding.challenge.kafka.avro.model.EmployeeAvroModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeConsumer implements KafkaConsumer<String, EmployeeAvroModel> {

  private static final Logger LOG = LoggerFactory.getLogger(EmployeeConsumer.class);

  private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

  private final KafkaAdminClient kafkaAdminClient;

  private final KafkaConfigData kafkaConfigData;

  private final KafkaConsumerConfigData kafkaConsumerConfigData;

  public EmployeeConsumer(KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry,
                          KafkaAdminClient kafkaAdminClient,
                          KafkaConfigData kafkaConfigData,
                          KafkaConsumerConfigData kafkaConsumerConfigData) {
    this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
    this.kafkaAdminClient = kafkaAdminClient;
    this.kafkaConfigData = kafkaConfigData;
    this.kafkaConsumerConfigData = kafkaConsumerConfigData;
  }

  @EventListener
  public void onAppStarted(ApplicationStartedEvent event) {
    kafkaAdminClient.checkTopicsCreated();
    LOG.info("Topics with name {} are ready for operations", kafkaConfigData.getTopicNamesToCreate().toArray());
    kafkaListenerEndpointRegistry.getListenerContainer(kafkaConsumerConfigData.getConsumerGroupId()).start();//auto-startup: true
  }

  @Override
  @KafkaListener(id = "${kafka-consumer-config.consumer-group-id}", topics = "${kafka-config.topic-name}")
  public void receive(List<EmployeeAvroModel> messages,
                      List<Integer> keys,
                      List<Integer> partitions,
                      List<Long> offsets) {
    LOG.info("{} number of message received with keys {}, partitions {} and offsets {}",
        messages.size(),
        keys,
        partitions,
        offsets);

    for (EmployeeAvroModel employeeAvroModel : messages) {
      LOG.info("{}", employeeAvroModel);
    }
  }

}
