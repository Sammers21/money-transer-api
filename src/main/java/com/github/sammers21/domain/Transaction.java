package com.github.sammers21.domain;

/**
 * Represent a transaction.
 */
public interface Transaction {

  /**
   * @return id of the transaction
   */
  Long transactionId();

  /**
   * @return account from which money came, {@code null} in case of payback
   */
  Account from();

  /**
   * @return account for which money came, {@code null} in case of drawback
   */
  Account to();

  /**
   * @return amount of money, related to the transaction
   */
  Long amountOfMoney();

  /**
   * @return when the transaction happen
   */
  Long timestamp();
}
