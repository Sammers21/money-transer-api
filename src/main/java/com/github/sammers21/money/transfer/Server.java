package com.github.sammers21.money.transfer;

import com.github.sammers21.money.transfer.domain.User;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

  private final static String NICK_NAME = "nick_name";

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
    router.get(String.format("/user/:%s", NICK_NAME)).handler(ctx -> {
      String nickName = getNickName(ctx);
      User user = storage.getOrCreateUser(nickName);
      ctx.response().end(JsonObject.mapFrom(user).encodePrettily());
    });
    httpServer.requestHandler(router).listen(port);
    log.info("Started on port {}", port);
  }

  public synchronized void stop() {
    httpServer.close();
  }

  private String getNickName(RoutingContext ctx) {
    return ctx.request().getParam(NICK_NAME);
  }
}
