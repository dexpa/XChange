package org.knowm.xchange.huobi.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HuobiAccount {

  private final long id;
  private final String state;
  private final String type;
  private final String userID;

  public HuobiAccount(
      @JsonProperty("id") long id,
      @JsonProperty("state") String state,
      @JsonProperty("type") String type,
      @JsonProperty("user-id") String userID) {
    this.id = id;
    this.state = state;
    this.type = type;
    this.userID = userID;
  }

  public long getId() {
    return id;
  }

  public String getState() {
    return state;
  }

  public String getType() {
    return type;
  }

  public String getUserID() {
    return userID;
  }

  @Override
  public String toString() {
    return String.format(
        "[id = %s, state = %s, type = %s, userID = %s]",
        getId(), getState(), getType(), getUserID());
  }
}
