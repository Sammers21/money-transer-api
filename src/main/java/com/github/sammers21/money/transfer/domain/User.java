package com.github.sammers21.money.transfer.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

  @JsonProperty("amount_of_money")
  private long amountOfMoney;

  @JsonProperty("nick_name")
  private String mickName;

  public User(long amountOfMoney, String mickName) {
    this.amountOfMoney = amountOfMoney;
    this.mickName = mickName;
  }

  public User() {
  }

  public long getAmountOfMoney() {
    return amountOfMoney;
  }

  public String getMickName() {
    return mickName;
  }
}
