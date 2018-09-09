package com.github.sammers21;

import io.vertx.core.json.JsonObject;

public class Account {

  private static final String ACCOUNT_ID_FIELD = "account_id";
  private static final String ACCOUNT_MONEY_FIELD = "money";

  public final Long money;
  public final Long accountId;

  public Account(Long money, Long accountId) {
    this.money = money;
    this.accountId = accountId;
  }

  static Account fromJson(JsonObject jsonObject) {
    return new Account(jsonObject.getLong(ACCOUNT_MONEY_FIELD), jsonObject.getLong(ACCOUNT_ID_FIELD));
  }
}
