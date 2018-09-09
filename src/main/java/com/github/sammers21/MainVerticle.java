package com.github.sammers21;

import com.github.sammers21.domain.Account;
import com.github.sammers21.domain.Transaction;
import com.github.sammers21.domain.User;
import com.github.sammers21.domain.impl.TransactionImpl;
import com.github.sammers21.handlers.SumParamHandler;
import com.github.sammers21.handlers.UserAccountHandler;
import com.github.sammers21.storage.UserStorage;
import com.github.sammers21.storage.impl.InMemoryUserStorage;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

  private static final Logger log = LoggerFactory.getLogger(MainVerticle.class);

  private static final String USER_ID = "user_id";
  private static final String ACCOUNT_ID = "account_id";
  private static final String SUM = "sum";
  private static final String TO_USER = "to_user";
  private static final String TO_ACCOUNT = "to_account";
  private static final int PORT = 8080;

  private final UserStorage storage = new InMemoryUserStorage();

  @Override
  public void start(Future<Void> startFuture) {
    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);
    router.route().handler(this::allResponsesIsJson);
    router.put("/user/:user_id/create-account").handler(this::createNewAccount);
    router.get("/user/:user_id/account/:account_id").handler(this::getAccount);
    router.post("/user/:user_id/account/:account_id/withdraw").handler(this::withdraw);
    router.post("/user/:user_id/account/:account_id/contribute").handler(this::contribute);
    router.post("/user/:user_id/account/:account_id/transfer-money").handler(this::transferMoney);
    server.requestHandler(router::accept).listen(PORT, asyncResult -> {
      if (asyncResult.succeeded()) {
        startFuture.complete();
        log.info("Money transfer app is listening on port " + PORT);
      } else {
        startFuture.fail(asyncResult.cause());
        log.error("Unable to start app on port " + PORT);
      }
    });
  }

  private void transferMoney(RoutingContext routingContext) {
    HttpServerResponse response = routingContext.response();
    withTransferToParams(routingContext,
      (toUser, toAccount) ->
        withSumQueryParam(routingContext,
          sum ->
            withAccount(routingContext,
              (fromUser, fromAccount) ->
                toAccount.transactionally(() ->
                  fromAccount.transactionally(() -> {
                      Transaction transaction = new TransactionImpl(fromAccount, toAccount, sum);
                      if (fromAccount.money() >= sum) {
                        fromAccount.transactions().put(transaction.transactionId(), transaction);
                        toAccount.transactions().put(transaction.transactionId(), transaction);
                        response.setStatusCode(200).end(Responses.success().toBuffer());
                      } else {
                        response.setStatusCode(200).end(Responses.failure().toBuffer());
                      }
                    }
                  )
                )
            )
        )
    );
  }


  private void contribute(RoutingContext routingContext) {
    HttpServerResponse response = routingContext.response();
    withSumQueryParam(routingContext,
      sum ->
        withAccount(routingContext, (user, account) -> {
          account.transactionally(() -> {
            Transaction transaction = new TransactionImpl(null, account, sum);
            account.transactions().put(transaction.transactionId(), transaction);
          });
          response.setStatusCode(200).end(Responses.success().toBuffer());
        })
    );
  }

  private void withdraw(RoutingContext routingContext) {
    HttpServerResponse response = routingContext.response();
    withSumQueryParam(routingContext,
      sum ->
        withAccount(routingContext, (user, account) ->
          account.transactionally(() -> {
              Transaction transaction = new TransactionImpl(account, null, sum);
              if (account.money() < sum) {
                response.setStatusCode(200).end(Responses.failure().toBuffer());
              } else {
                account.transactions().put(transaction.transactionId(), transaction);
                response.setStatusCode(200).end(Responses.success().toBuffer());
              }
            }
          )
        )
    );
  }


  private void withAccount(RoutingContext routingContext, UserAccountHandler handler) {
    HttpServerResponse response = routingContext.response();
    try {
      String userId = routingContext.request().getParam(USER_ID);
      Long accountId = Long.parseLong(routingContext.request().getParam(ACCOUNT_ID));
      if (!storage.isUserExist(userId)) {
        String message = String.format("User with id '%s' does not exist", userId);
        response.setStatusCode(404).end(Responses.message(message).toBuffer());
        return;
      }
      User user = storage.createUserIfNotExist(userId);
      Account account = user.accountById(accountId);
      if (account == null) {
        String message = String.format("Account with id '%d' does not exist", accountId);
        response.setStatusCode(404).end(Responses.message(message).toBuffer());
        return;
      }
      handler.handle(user, account);
    } catch (NumberFormatException e) {
      response.setStatusCode(400).end(Responses.message("Please provide valid 'account_id' param number").toBuffer());
    }
  }

  private void withSumQueryParam(RoutingContext routingContext, SumParamHandler handler) {
    HttpServerResponse response = routingContext.response();
    try {
      String sum = routingContext.request().getParam(SUM);
      if (sum == null) {
        String responseMessage = "'sum' query param should be present";
        response.setStatusCode(400).end(Responses.message(responseMessage).toBuffer());
        return;
      }
      Long money = Long.parseLong(sum);
      handler.handle(money);
    } catch (NumberFormatException e) {
      response.setStatusCode(400).end(Responses.message("Please provide valid 'sum' param number").toBuffer());
    }
  }


  private void withTransferToParams(RoutingContext routingContext, UserAccountHandler handler) {
    HttpServerResponse response = routingContext.response();
    try {
      String userId = routingContext.request().getParam(TO_USER);
      Long accountId = Long.parseLong(routingContext.request().getParam(TO_ACCOUNT));
      if (!storage.isUserExist(userId)) {
        String message = String.format("User with id '%s' does not exist", userId);
        response.setStatusCode(404).end(Responses.message(message).toBuffer());
        return;
      }
      User user = storage.createUserIfNotExist(userId);
      Account account = user.accountById(accountId);
      if (account == null) {
        String message = String.format("Account with id '%d' does not exist", accountId);
        response.setStatusCode(404).end(Responses.message(message).toBuffer());
        return;
      }
      handler.handle(user, account);
    } catch (NumberFormatException e) {
      response.setStatusCode(400).end(Responses.message("Please provide valid 'to_account' param number").toBuffer());
    }
  }

  private void allResponsesIsJson(RoutingContext ctx) {
    ctx.response().putHeader("Content-Type", "application/json");
    ctx.next();
  }

  private void createNewAccount(RoutingContext routingContext) {
    String userId = routingContext.request().getParam(USER_ID);
    User user = storage.createUserIfNotExist(userId);
    Account account = user.createAccount();
    routingContext.response().end(
      account.asJson().toBuffer()
    );
  }

  private void getAccount(RoutingContext routingContext) {
    withAccount(routingContext, (user, account) ->
      routingContext.response().setStatusCode(200).end(account.asJson().toBuffer())
    );
  }
}
