package com.github.sammers21.domain.impl;

import com.github.sammers21.domain.Account;
import com.github.sammers21.domain.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class UserImpl implements User {

  private final String userId;
  private final AtomicLong accountCounter;
  private final Map<Long, Account> accountById;

  public UserImpl(String userId) {
    this.userId = userId;
    accountCounter = new AtomicLong(1);
    accountById = new ConcurrentHashMap<>();
  }

  @Override
  public String userId() {
    return userId;
  }

  @Override
  public Account createAccount() {
    AccountImpl account = new AccountImpl(accountCounter.getAndIncrement());
    accountById.put(account.accountId(), account);
    return account;
  }

  @Override
  public Account accountById(Long accountId) {
    return accountById.get(accountId);
  }
}
