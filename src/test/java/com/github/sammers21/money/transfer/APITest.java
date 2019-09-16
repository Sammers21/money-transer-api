package com.github.sammers21.money.transfer;

import org.junit.Assert;
import org.junit.Test;

public class APITest extends Base {

  @Test
  public void transferTest() {
    long sum = 10L;
    assertBooleanResponse(transfer(sum, NICK1, NICK2), false);
    assertBooleanResponse(transfer(0L, NICK1, NICK2), true);
    Assert.assertEquals(0, getUserByNick(NICK1).getAmountOfMoney());
    Assert.assertEquals(0, getUserByNick(NICK2).getAmountOfMoney());
    assertBooleanResponse(contribute(sum, NICK1), true);
    assertBooleanResponse(transfer(sum, NICK1, NICK2), true);
    Assert.assertEquals(0, getUserByNick(NICK1).getAmountOfMoney());
    Assert.assertEquals(sum, getUserByNick(NICK2).getAmountOfMoney());
    assertBooleanResponse(transfer(sum, NICK2, NICK1), true);
    Assert.assertEquals(sum, getUserByNick(NICK1).getAmountOfMoney());
    Assert.assertEquals(0, getUserByNick(NICK2).getAmountOfMoney());
  }

  @Test
  public void contributeTest() {
    long sum = 10L;
    assertBooleanResponse(contribute(sum, NICK1), true);
    Assert.assertEquals(sum, getUserByNick(NICK1).getAmountOfMoney());
  }

  @Test
  public void withdrawTest() {
    long sum = 10L;
    assertBooleanResponse(withdraw(sum, NICK1), false);
    assertBooleanResponse(contribute(sum, NICK1), true);
    Assert.assertEquals(sum, getUserByNick(NICK1).getAmountOfMoney());
    assertBooleanResponse(withdraw(sum, NICK1), true);
    Assert.assertEquals(0L, getUserByNick(NICK1).getAmountOfMoney());
    assertBooleanResponse(withdraw(sum, NICK1), false);
    Assert.assertEquals(0L, getUserByNick(NICK1).getAmountOfMoney());
  }

}
