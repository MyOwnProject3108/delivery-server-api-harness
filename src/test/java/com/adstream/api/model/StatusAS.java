package com.adstream.api.model;

/**
 * Created by natla on 26/07/2016.
 */
public class StatusAS extends BodyBuilder {

  private String oldStatus;
  private String newStatus;
  private String timestamp;

  public String getOldStatus() {
    return oldStatus;
  }

  public StatusAS withOldStatus(String oldStatus) {
    this.oldStatus = oldStatus;
    return this;
  }

  public String getNewStatus() {
    return newStatus;
  }

  public StatusAS withNewStatus(String newStatus) {
    this.newStatus = newStatus;
    return this;
  }

}
