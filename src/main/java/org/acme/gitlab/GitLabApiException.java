package org.acme.gitlab;

public class GitLabApiException extends RuntimeException {
  private final int status;

  public GitLabApiException(int status, String message) {
    super(message);
    this.status = status;
  }

  public int getStatus() {
    return status;
  }
}