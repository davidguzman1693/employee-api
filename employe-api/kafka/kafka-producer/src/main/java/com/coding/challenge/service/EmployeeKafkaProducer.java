package com.coding.challenge.service;

import com.coding.challenge.kafka.avro.model.EmployeeAvroModel;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.PreDestroy;
import java.io.Serializable;

/**
 * Implementation of {@link KafkaProducer}.
 *
 * @author dguzman.
 */
@Service
public class EmployeeKafkaProducer implements KafkaProducer<String, EmployeeAvroModel> {

  private static final Logger LOG = LoggerFactory.getLogger(EmployeeKafkaProducer.class);

  private KafkaTemplate<String, EmployeeAvroModel> kafkaTemplate;

  public EmployeeKafkaProducer(KafkaTemplate<String, EmployeeAvroModel> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @Override
  public void send(String topicName, String key, EmployeeAvroModel message) {
    LOG.info("Sending message {} to topic {}", message, topicName);
    //ListenableFuture register callback methods for handling events when response returns.
    ListenableFuture<SendResult<String, EmployeeAvroModel>> kafkaResultFuture = kafkaTemplate.send(topicName, key, message);
    addCallback(topicName, message, kafkaResultFuture);
  }

  @PreDestroy
  public void close() {
    if (kafkaTemplate != null) {
      LOG.info("Closing kafka producer!");
      kafkaTemplate.destroy();
    }
  }

  private static void addCallback(String topicName,
                                  EmployeeAvroModel message,
                                  ListenableFuture<SendResult<String, EmployeeAvroModel>> kafkaResultFuture) {
    kafkaResultFuture.addCallback(new ListenableFutureCallback<SendResult<String, EmployeeAvroModel>>() {
      @Override
      public void onFailure(Throwable throwable) {
        LOG.error("Error while sending message {} to topic {}", message, topicName, throwable);
      }

      @Override
      public void onSuccess(SendResult<String, EmployeeAvroModel> result) {
        RecordMetadata metadata = result.getRecordMetadata();
        LOG.info("Received new metadata. Topic: {}; Partition {}; Offset {}; Timestamp {}, at time {}",
            metadata.topic(),
            metadata.partition(),
            metadata.offset(),
            metadata.timestamp(),
            System.nanoTime());
      }
    });
  }
}
