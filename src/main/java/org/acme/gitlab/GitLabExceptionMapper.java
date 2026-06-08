package org.acme.gitlab;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GitLabExceptionMapper implements ResponseExceptionMapper<GitLabApiException> {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Override
  public boolean handles(int status, MultivaluedMap<String, Object> headers) {
    return status >= 400;   // map every error status to our exception
  }

  @Override
  public GitLabApiException toThrowable(Response response) {
    int status = response.getStatus();
    String body = response.readEntity(String.class);
    return new GitLabApiException(status, extractMessage(body));
  }

  private String extractMessage(String body) {
    if (body == null || body.isBlank()) return "No error body";
    try {
      JsonNode root = MAPPER.readTree(body);
      // GitLab uses "message" (string OR object) or "error"
      if (root.has("message")) return root.get("message").toString();
      if (root.has("error"))   return root.get("error").asText();
    } catch (Exception ignored) {
      // not JSON — return the raw body
    }
    return body;
  }

  public record Issue(
    Long id,
    Long iid,                     // the per-project issue number
    String title,
    String state,
    @JsonProperty("web_url") String webUrl
  ) {}
}