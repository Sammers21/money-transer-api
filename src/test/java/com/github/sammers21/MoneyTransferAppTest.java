package com.github.sammers21;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class MoneyTransferAppTest {

  Vertx vertx = Vertx.vertx();
  String deploymentId;
  TransferApiClient transferApiClient = new TransferApiClient(vertx, "http://localhost:8080");
  String userName1 = "userName1";
  String userName2 = "userName1";

  private static final String FAIL = "FAIL";
  private static final String SUCCESS = "SUCCESS";

  @Test
  public void testWithdrawAndContribute(TestContext context) throws InterruptedException {
    long contribSum = 500L;
    long withdrawSum = 100L;
    Account account1 = transferApiClient.createAccount(userName1);
    Message contribute = transferApiClient.contribute(userName1, account1.accountId, contribSum);
    context.assertTrue(contribute.message.equals(SUCCESS));
    Account accountWithContrib = transferApiClient.getAccount(userName1, account1.accountId);
    context.assertTrue(accountWithContrib.money == contribSum);
    Message withdraw = transferApiClient.withdraw(userName1, account1.accountId, withdrawSum);
    context.assertTrue(withdraw.message.equals(SUCCESS));
    Account accountWithContribAndWithdraw = transferApiClient.getAccount(userName1, account1.accountId);
    context.assertTrue(accountWithContribAndWithdraw.money == contribSum - withdrawSum);
    Message failedWithdraw = transferApiClient.withdraw(userName1, account1.accountId, 700L);
    context.assertTrue(failedWithdraw.message.equals(FAIL));
  }

  @Test
  public void testMoneyTransferRaceSuccess(TestContext context) throws InterruptedException {
    for (int i = 0; i < 1000; i++) {
      testTransferRaceSuccess(context, userName1 + i, userName2 + i);
    }
  }

  @Test
  public void testMoneyTransferRaceFailure(TestContext context) throws InterruptedException {
    for (int i = 0; i < 1000; i++) {
      testTransferRaceFailure(context, userName1 + i, userName2 + i);
    }
  }


  private void testTransferRaceSuccess(TestContext context, String user1, String user2) throws InterruptedException {
    Account account1 = transferApiClient.createAccount(user1);
    Account account2 = transferApiClient.createAccount(user2);
    Message contribute1 = transferApiClient.contribute(user1, account1.accountId, 500L);
    context.assertTrue(contribute1.message.equals(SUCCESS));
    Message contribute2 = transferApiClient.contribute(user2, account2.accountId, 100L);
    context.assertTrue(contribute2.message.equals(SUCCESS));
    Async async = context.async(2);
    transferApiClient.transferMoneyAsync(user1, account1.accountId, user2, account2.accountId, 200L)
      .setHandler(res -> {
        if (res.succeeded()) {
          async.countDown();
        }
      });
    transferApiClient.transferMoneyAsync(user1, account1.accountId, user2, account2.accountId, 300L)
      .setHandler(res -> {
        if (res.succeeded()) {
          async.countDown();
        }
      });

    async.await(10000L);
    Account account1Updated = transferApiClient.getAccount(user1, account1.accountId);
    context.assertTrue(account1Updated.money == 0L);
    Account account2Updated = transferApiClient.getAccount(user2, account2.accountId);
    context.assertTrue(account2Updated.money == 600L);
  }

  private void testTransferRaceFailure(TestContext context, String user1, String user2) throws InterruptedException {
    Account account1 = transferApiClient.createAccount(user1);
    Account account2 = transferApiClient.createAccount(user2);
    Message contribute1 = transferApiClient.contribute(user1, account1.accountId, 500L);
    context.assertTrue(contribute1.message.equals(SUCCESS));
    Message contribute2 = transferApiClient.contribute(user2, account2.accountId, 100L);
    context.assertTrue(contribute2.message.equals(SUCCESS));
    Async async = context.async(2);
    transferApiClient.transferMoneyAsync(user1, account1.accountId, user2, account2.accountId, 300L)
      .setHandler(res -> {
        if (res.succeeded()) {
          async.countDown();
        }
      });
    transferApiClient.transferMoneyAsync(user1, account1.accountId, user2, account2.accountId, 400L)
      .setHandler(res -> {
        if (res.succeeded()) {
          async.countDown();
        }
      });

    async.await(10000L);
    Account account1Updated = transferApiClient.getAccount(user1, account1.accountId);
    Account account2Updated = transferApiClient.getAccount(user2, account2.accountId);
    if (account1Updated.money == 200L) {
      context.assertTrue(account2Updated.money == 400L);
    } else {
      context.assertTrue(account2Updated.money == 500L);
    }
  }

  @Before
  public void before(TestContext context) {
    Async async = context.async();
    vertx.deployVerticle(new MainVerticle(), event -> {
      async.countDown();
      deploymentId = event.result();
    });
    async.await();
  }

  @After
  public void after(TestContext context) {
    Async async = context.async();
    vertx.undeploy(deploymentId, event -> async.countDown());
    async.await();
  }
}
