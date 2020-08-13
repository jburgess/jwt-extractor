package com.jburgess.jwt.model;

import javax.json.JsonObject;

public class TokenPayload {

  public static final String JSON_LABEL = "token";

  public TokenPayload(String token) {
    this.token = token;
  }

  private String token;

  @Override
  public String toString() {
    return "TokenPayload{" + "token='" + token + '\'' + '}';
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }



  /**
   * Converts a JSON object (typically read from the request payload)
   * into a {@code GreetingMessage}.
   *
   * @param jsonObject the {@link JsonObject} to convert.
   * @return {@code GreetingMessage} set according to the provided object
   */
  public static TokenPayload fromRest(JsonObject jsonObject) {
    return new TokenPayload(jsonObject.getString(JSON_LABEL));
  }

}
