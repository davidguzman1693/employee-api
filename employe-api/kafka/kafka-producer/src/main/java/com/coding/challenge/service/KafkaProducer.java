package com.coding.challenge.service;

import org.apache.avro.specific.SpecificRecordBase;

import java.io.Serializable;

/**
 * Interface for the Kafka producer.
 *
 * @author dguzman.
 */
public interface KafkaProducer<K extends Serializable, V extends SpecificRecordBase> {
  /**
   * Sends the message to a given Topic in Kafka.
   *
   * @param topicName the name of topic.
   * @param key       to be used in event.
   * @param message   to send.
   */
  void send(String topicName, K key, V message);
}
