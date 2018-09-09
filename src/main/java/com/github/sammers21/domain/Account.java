package com.github.sammers21.domain;

import io.vertx.core.json.JsonObject;

import java.util.Map;

/**
 * Represent an account.
 */
public interface Account {

  /**
   * @return a account id
   */
  Long accountId();

  /**
   * @return transactions related to the account
   */
  Map<Long, Transaction> transactions();

  /**
   * @param transactionalCode execute some code transactionallу
   */
  void transactionally(Runnable transactionalCode);

  /**
   * @return current amount of money on the account
   */
  Long money();

  /**
   * @return a json representation of the account
   */
  JsonObject asJson();
}
