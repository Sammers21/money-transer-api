package com.github.sammers21.money.transfer;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

  public final static String NICK_NAME = "nick_name";
  public final static String SUM = "sum";
  public final static String TO = "to";

  private static final Logger log = LoggerFactory.getLogger(Server.class);

  private final Integer port;

  private final Vertx vertx;
  private HttpServer httpServer;
  private final Storage storage;

  public Server(Vertx vertx, Storage storage, Integer port) {
    this.vertx = vertx;
    this.storage = storage;
    this.port = port;
  }

  public synchronized void start() {
    httpServer = vertx.createHttpServer();
    var router = Router.router(vertx);
    router.route(String.format("/user/:%s", NICK_NAME)).handler(ctx ->
      produceResponseObject(
        ctx,
        withString(ctx, NICK_NAME)
          .map(storage::getOrCreateUser)
      )
    );
    router.route(String.format("/user/:%s/contribute", NICK_NAME)).handler(ctx -> {
      produceResponseJson(
        ctx,
        nickAndSum(ctx).map(nickAndSum -> {
          String nick = nickAndSum.getValue0();
          Long sum = nickAndSum.getValue1();
          return operationResult(storage.contribute(nick, sum));
        })
      );
    });
    router.route(String.format("/user/:%s/withdraw", NICK_NAME)).handler(ctx -> {
      produceResponseJson(
        ctx,
        nickAndSum(ctx).map(nickAndSum -> {
          String nick = nickAndSum.getValue0();
          Long sum = nickAndSum.getValue1();
          return operationResult(storage.withdraw(nick, sum));
        })
      );
    });
    router.route(String.format("/user/:%s/transfer", NICK_NAME)).handler(ctx -> {
      produceResponseJson(
        ctx,
        nickAndSum(ctx).flatMap(nickAndSum ->
          withString(ctx, TO).map(to -> {
            String from = nickAndSum.getValue0();
            Long sum = nickAndSum.getValue1();
            return operationResult(storage.transferMoney(from, to, sum));
          }))
      );
    });
    httpServer.requestHandler(router).rxListen(port).blockingGet();
    log.info("Started on port {}", port);
  }

  private Single<Pair<String, Long>> nickAndSum(RoutingContext ctx) {
    return withString(ctx, NICK_NAME)
      .flatMap(nick -> withLong(ctx, SUM).map(sum -> new Pair<>(nick, sum)));
  }

  private JsonObject errorJson(String message) {
    return new JsonObject().put("error", message);
  }

  private JsonObject operationResult(boolean res) {
    return new JsonObject().put("operation_result", res);
  }

  public synchronized void stop() {
    httpServer.rxClose().blockingGet();
  }

  private Single<Long> withLong(RoutingContext ctx, String paramName) {
    try {
      return Single.just(Long.valueOf(ctx.request().getParam(paramName)));
    } catch (NumberFormatException e) {
      return Single.error(new IllegalStateException(String.format("Param '%s' of type long is not specified", paramName)));
    }
  }

  private Single<String> withString(RoutingContext ctx, String paramName) {
    String paramValue = ctx.request().getParam(paramName);
    if (paramValue == null) {
      return Single.error(new IllegalStateException(String.format("Param '%s' of type string is not specified", paramName)));
    } else {
      return Single.just(paramValue);
    }
  }

  private void produceResponseObject(RoutingContext ctx, Single<Object> response) {
    Single<JsonObject> map = response.map(JsonObject::mapFrom);
    produceResponseJson(ctx, map);
  }

  private void produceResponseJson(RoutingContext ctx, Single<JsonObject> response) {
    response.subscribe(
      ob -> ctx.response().end(ob.encodePrettily()),
      error -> ctx.response().setStatusCode(400).end(errorJson(error.getMessage()).encodePrettily())
    );
  }
}
