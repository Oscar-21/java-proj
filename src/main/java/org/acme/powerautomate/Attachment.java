package org.acme.powerautomate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Attachment(
  String id,
  @JsonProperty("@odata.type")
  String odataType,
  String lastModifiedDateTime,
  String name,
  String contentType,
  Integer size,
  Boolean isInline,
  String contentBytes
) {}
