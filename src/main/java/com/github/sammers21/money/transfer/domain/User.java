package com.github.sammers21.money.transfer.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

  @JsonProperty("amount_of_money")
  private final Long amountOfMoney;

  @JsonProperty("nick_name")
  private final String mickName;

  public User(Long amountOfMoney, String mickName) {
    this.amountOfMoney = amountOfMoney;
    this.mickName = mickName;
  }

  public Long getAmountOfMoney() {
    return amountOfMoney;
  }

  public String getMickName() {
    return mickName;
  }
}
