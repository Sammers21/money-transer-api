package com.github.sammers21.money.transfer;

import com.github.sammers21.money.transfer.impl.StorageImpl;
import io.vertx.reactivex.core.Vertx;

public class Main {

  private final static int DEFAULT_PORT = 8080;

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    new Server(vertx, new StorageImpl(), DEFAULT_PORT).start();
  }
}
