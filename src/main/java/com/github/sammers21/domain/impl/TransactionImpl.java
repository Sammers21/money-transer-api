package com.github.sammers21.domain.impl;

import com.github.sammers21.domain.Account;

import java.util.concurrent.atomic.AtomicLong;

public class TransactionImpl implements com.github.sammers21.domain.Transaction {

  private static final AtomicLong transactionIdAutoGen = new AtomicLong(1);

  private final Long transactionId;
  private final Account from;
  private final Account to;
  private final Long amountOfMoney;
  private final Long timestamp;

  public TransactionImpl(Account from, Account to, Long amountOfMoney) {
    this.transactionId = transactionIdAutoGen.getAndIncrement();
    this.from = from;
    this.to = to;
    this.amountOfMoney = amountOfMoney;
    this.timestamp = System.currentTimeMillis();
  }

  @Override
  public Long transactionId() {
    return transactionId;
  }

  @Override
  public Account from() {
    return from;
  }

  @Override
  public Account to() {
    return to;
  }

  @Override
  public Long amountOfMoney() {
    return amountOfMoney;
  }

  @Override
  public Long timestamp() {
    return timestamp;
  }

}
