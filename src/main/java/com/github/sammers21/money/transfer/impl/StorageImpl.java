package com.github.sammers21.money.transfer.impl;

import com.github.sammers21.money.transfer.Storage;
import com.github.sammers21.money.transfer.domain.User;
import io.reactivex.functions.Function3;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;

public class StorageImpl implements Storage {

  private final Map<String, Long> userNickNameAndMoney = new ConcurrentHashMap<>();

  @Override
  public User getOrCreateUser(String nickName) {
    userNickNameAndMoney.compute(nickName, (nick, sum) -> Objects.requireNonNullElse(sum, 0L));
    Long useMoney = userNickNameAndMoney.get(nickName);
    return new User(useMoney, nickName);
  }

  @Override
  public boolean transferMoney(String fromUserNickName, String toUserNickName, Long sum) {
    getOrCreateUser(fromUserNickName);
    getOrCreateUser(toUserNickName);
    boolean withdraw = withdraw(fromUserNickName, sum);
    if (withdraw) {
      return contribute(toUserNickName, sum);
    } else {
      return false;
    }
  }

  @Override
  public boolean withdraw(String fromUserNickName, Long sum) {
    return processTransaction(fromUserNickName, sum, (transactionSuccess, curMoney) -> {
      long afterTransactionMoney = curMoney - sum;
      if (afterTransactionMoney < 0) {
        transactionSuccess.set(false);
        return curMoney;
      } else {
        transactionSuccess.set(true);
        return afterTransactionMoney;
      }
    });
  }

  @Override
  public boolean contribute(String fromUserNickName, Long sum) {
    return processTransaction(fromUserNickName, sum, (transactionSuccess, curMoney) -> {
      transactionSuccess.set(true);
      return curMoney + sum;
    });
  }

  private boolean processTransaction(String fromUserNickName, Long sum, BiFunction<AtomicBoolean, Long, Long> transactionFunction) {
    if (sum < 0) {
      return false;
    }
    getOrCreateUser(fromUserNickName);
    AtomicBoolean res = new AtomicBoolean(false);
    userNickNameAndMoney.computeIfPresent(fromUserNickName, (nick, curMoneySum) -> transactionFunction.apply(res, curMoneySum));
    return res.get();
  }
}
