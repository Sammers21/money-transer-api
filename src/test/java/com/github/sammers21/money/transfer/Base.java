package com.github.sammers21.money.transfer;

import com.github.sammers21.money.transfer.domain.User;
import com.github.sammers21.money.transfer.impl.StorageImpl;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.client.WebClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.io.IOException;
import java.net.ServerSocket;

import static com.github.sammers21.money.transfer.Server.SUM;
import static com.github.sammers21.money.transfer.Server.TO;

public class Base {

  public final static String NICK1 = "pavel";
  public final static String NICK2 = "dimitry";

  private final Vertx vertx = Vertx.vertx();

  private Server server;

  protected int serverPort;
  protected WebClient webClient;

  @Before
  public void before() {
    serverPort = findFreePort();
    server = new Server(vertx, new StorageImpl(), serverPort);
    server.start();
    webClient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(serverPort).setDefaultHost("localhost"));
  }

  @After
  public void after() {
    server.start();
  }

  private static int findFreePort() {
    ServerSocket socket = null;
    try {
      socket = new ServerSocket(0);
      socket.setReuseAddress(true);
      int port = socket.getLocalPort();
      try {
        socket.close();
      } catch (IOException e) {
        // Ignore IOException on close()
      }
      return port;
    } catch (IOException e) {
    } finally {
      if (socket != null) {
        try {
          socket.close();
        } catch (IOException e) {
        }
      }
    }
    throw new IllegalStateException("Could not find a free TCP/IP port");
  }


  public void assertBooleanResponse(JsonObject object, boolean value) {
    Assert.assertEquals(object.getBoolean("operation_result"), value);
  }

  protected JsonObject transfer(Long sum, String from, String to) {
    return webClient.get(String.format("/user/%s/transfer", from))
      .addQueryParam(SUM, String.valueOf(sum))
      .addQueryParam(TO, to)
      .rxSend()
      .blockingGet()
      .bodyAsJsonObject();
  }

  protected JsonObject contribute(Long sum, String nick) {
    return webClient.get(String.format("/user/%s/contribute", nick))
      .addQueryParam(SUM, String.valueOf(sum))
      .rxSend()
      .blockingGet()
      .bodyAsJsonObject();
  }

  protected JsonObject withdraw(Long sum, String nick) {
    return webClient.get(String.format("/user/%s/withdraw", nick))
      .addQueryParam(SUM, String.valueOf(sum))
      .rxSend()
      .blockingGet()
      .bodyAsJsonObject();
  }

  protected User getUserByNick(String nick) {
    return webClient.get(String.format("/user/%s", nick))
      .rxSend()
      .blockingGet()
      .bodyAsJsonObject()
      .mapTo(User.class);
  }
}
