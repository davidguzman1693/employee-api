package com.coding.challenge.init;

/**
 * Stream initializer to check Kafka status.
 *
 * @author dguzman.
 */
public interface StreamInitializer {
  
  /**
   * Initialize kafka checking.
   */
  void init();
}
