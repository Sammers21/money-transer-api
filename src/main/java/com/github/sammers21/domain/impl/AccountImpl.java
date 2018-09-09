package com.github.sammers21.domain.impl;

import com.github.sammers21.domain.Account;
import com.github.sammers21.domain.Transaction;
import io.vertx.core.json.JsonObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class AccountImpl implements Account {

  private static final String ACCOUNT_ID_FIELD = "account_id";
  private static final String ACCOUNT_MONEY_FIELD = "money";
  private static final long NON_EXISTED_TRANSACTION_ID = -1L;

  private final Long accountId;
  private AtomicLong money;
  private long lastHandledTransactionId = NON_EXISTED_TRANSACTION_ID;
  private final TreeMap<Long, Transaction> transactions = new TreeMap<>();

  public AccountImpl(Long accountId) {
    this.accountId = accountId;
    this.money = new AtomicLong(0L);
  }

  @Override
  public Long accountId() {
    return accountId;
  }

  @Override
  public Map<Long, Transaction> transactions() {
    return Collections.synchronizedMap(transactions);
  }

  @Override
  public synchronized void transactionally(Runnable transactionalCode) {
    transactionalCode.run();
  }

  @Override
  public synchronized Long money() {
    NavigableSet<Long> keySet = transactions.descendingKeySet();
    Iterator<Long> iterator = keySet.iterator();
    if (lastHandledTransactionId == NON_EXISTED_TRANSACTION_ID) {
      while (iterator.hasNext()) {
        handleTransaction(transactions.get(iterator.next()));
      }
    } else {
      Transaction transaction;
      while (iterator.hasNext() &&
        !((transaction = transactions.get(iterator.next())).transactionId() == lastHandledTransactionId)) {
        handleTransaction(transaction);
      }
    }

    if (transactions.size() != 0) {
      lastHandledTransactionId = keySet.iterator().next();
    }

    return money.get();
  }

  private void handleTransaction(Transaction transaction) {
    if (transaction.to() == this) {
      money.accumulateAndGet(transaction.amountOfMoney(), (left, right) -> left + right);
    } else {
      money.accumulateAndGet(transaction.amountOfMoney() * -1, (left, right) -> left + right);
    }
  }

  @Override
  public JsonObject asJson() {
    return new JsonObject()
      .put(ACCOUNT_ID_FIELD, accountId)
      .put(ACCOUNT_MONEY_FIELD, money());
  }
}
