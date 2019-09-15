package com.github.sammers21.money.transfer;

import com.github.sammers21.money.transfer.domain.User;

public interface Storage {

  User getOrCreateUser(String nickName);

  boolean transferMoney(String fromUserNickName, String toUserNickName, Long sum);

  boolean withdraw(String fromUserNickName, Long sum);

  boolean contribute(String fromUserNickName, Long sum);

}
