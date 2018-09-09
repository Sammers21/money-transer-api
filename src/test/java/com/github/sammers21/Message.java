package com.github.sammers21;

import io.vertx.core.json.JsonObject;

public class Message {

  private static final String MESSAGE = "message";

  public final String message;

  public Message(String message) {
    this.message = message;
  }

  static Message fromJson(JsonObject jsonObject) {
    return new Message(jsonObject.getString(MESSAGE));
  }
}
