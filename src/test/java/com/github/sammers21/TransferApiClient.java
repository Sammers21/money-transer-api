package com.github.sammers21;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class TransferApiClient {

  private static final Logger log = LoggerFactory.getLogger(TransferApiClient.class);

  Vertx vertx;
  WebClient webClient;
  String uri;

  public TransferApiClient(Vertx vertx, String baseUri) {
    this.vertx = vertx;
    webClient = WebClient.create(vertx);
    this.uri = baseUri;
  }

  public Account createAccount(String userId) throws InterruptedException {
    return Account.fromJson(
      jsonWithRequest(webClient.putAbs(String.format("%s/user/%s/create-account", uri, userId)))
    );
  }

  public Account getAccount(String userId, Long accountId) throws InterruptedException {
    return Account.fromJson(
      jsonWithRequest(webClient.getAbs(String.format("%s/user/%s/account/%d", uri, userId, accountId)))
    );
  }

  public Message withdraw(String userId, Long accountId, Long sum) throws InterruptedException {
    return
      Message.fromJson(
        jsonWithRequest(
          webClient.postAbs(String.format("%s/user/%s/account/%d/withdraw", uri, userId, accountId))
            .addQueryParam("sum", sum.toString())
        )
      );
  }

  public Message contribute(String userId, Long accountId, Long sum) throws InterruptedException {
    return Message.fromJson(
      jsonWithRequest(
        webClient.postAbs(String.format("%s/user/%s/account/%d/contribute", uri, userId, accountId))
          .addQueryParam("sum", sum.toString())
      )
    );
  }

  public Future<Message> transferMoneyAsync(String fromUser, Long fromAccount, String toUser, Long toAccount, Long sum) throws InterruptedException {

    Future<HttpResponse<Buffer>> reponseF = Future.future();
    webClient.postAbs(String.format("%s/user/%s/account/%d/transfer-money", uri, fromUser, fromAccount))
      .addQueryParam("sum", sum.toString())
      .addQueryParam("to_user", toUser)
      .addQueryParam("to_account", toAccount.toString())
      .send(reponseF);

    return reponseF.compose(response -> Future.succeededFuture(Message.fromJson(response.bodyAsJsonObject())));
  }

  private JsonObject jsonWithRequest(HttpRequest<Buffer> request) throws InterruptedException {
    AtomicReference<JsonObject> result = new AtomicReference<>(null);
    CountDownLatch countDownLatch = new CountDownLatch(1);
    request
      .send(response -> {
        if (response.succeeded()) {
          result.set(response.result().bodyAsJsonObject());
          countDownLatch.countDown();
        } else {
          log.error("error occurred", response.cause());
        }
      });
    countDownLatch.await();
    return result.get();
  }
}
