package com.github.sammers21;

import io.vertx.core.json.JsonObject;

public class Responses {

  private static final String MESSAGE = "message";
  private static final String FAIL = "FAIL";
  private static final String SUCCESS = "SUCCESS";

  static JsonObject message(String message) {
    return new JsonObject().put(MESSAGE, message);
  }

  public static JsonObject failure() {
    return message(FAIL);
  }

  public static JsonObject success() {
    return message(SUCCESS);
  }
}
